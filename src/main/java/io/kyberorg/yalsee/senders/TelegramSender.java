package io.kyberorg.yalsee.senders;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.user.AccountService;
import io.kyberorg.yalsee.telegram.TelegramBot;
import io.kyberorg.yalsee.telegram.messages.LoginVerificationMessage;
import io.kyberorg.yalsee.telegram.messages.PasswordResetMessage;
import io.kyberorg.yalsee.telegram.messages.TelegramMessage;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.users.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Sender, that sends tokens via Telegram.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender extends TokenSender {
    private static final String TAG = "[" + TelegramSender.class.getSimpleName() + "]";
    private final AccountService accountService;
    private final LinkService linkService;
    private final Configuration configuration;
    private final TelegramBot telegramBot;

    private static final String ERR_TOKEN_HAS_OWNER = "Token has no owner";
    private static final String ERR_NO_TELEGRAM_ACCOUNT = "Token owner has no telegram account";
    private static final String ERR_TELEGRAM_ACCOUNT_NOT_CONFIRMED =
            "Token owner has unconfirmed telegram account. Please confirm it first.";

    private static final String MSG_NO_NEED_TO_SEND_MESSAGE = "No need to send any message to given token type";
    private static final String MSG_NO_LONG_LINK_FOUND = "No long link found in template vars";

    /**
     * Prepares message and sends it to user's Telegram. This method sends message only to confirmed Telegram Account.
     *
     * @param token           {@link Token} to send.
     * @param telegramAccount string with account name, {@link TokenSender} should send to.
     * @return {@link OperationResult#success()} or with error and message.
     */
    @Override
    public OperationResult send(final Token token, final String telegramAccount) {
        //Account confirmed?
        OperationResult telegramAccountConfirmedCheck = tokenOwnerHasConfirmedTelegramAccount(token.getUser());
        if (telegramAccountConfirmedCheck.notOk()) {
            if (Objects.equals(telegramAccountConfirmedCheck.getResult(), OperationResult.BANNED)) {
                return OperationResult.banned().withMessage(ERR_TELEGRAM_ACCOUNT_NOT_CONFIRMED);
            } else {
                log.warn("{} Failed to define if token owner {} has confirmed telegram account or not. OpResult {}",
                        TAG, token.getUser(), telegramAccountConfirmedCheck);
                return telegramAccountConfirmedCheck;
            }
        }

        //Getting right telegram message to send
        TelegramMessage telegramMessage = getTelegramMessage(token, telegramAccount);
        if (telegramMessage == null) {
            log.info("{} Skipping sending: no need to send any message to given token type {}",
                    TAG, token.getTokenType());
            return OperationResult.success().withMessage(MSG_NO_NEED_TO_SEND_MESSAGE);
        }
        String templateFile = telegramMessage.getTemplate();
        Map<String, Object> templateVars = telegramMessage.getTemplateVars();

        //Shortify link, if needed
        if (templateVars.containsKey("link") && token.getTokenType().shouldCreateShortLink()) {
            OperationResult shortifyResult = shortifyLink(templateVars, token.getTokenType());
            if (shortifyResult.ok()) {
                templateVars.put("link", shortifyResult.getStringPayload());
            } else {
                log.warn("{} Shortification failed. OpResult: {}", TAG, shortifyResult);
            }
        }

        //compile template
        String compiledTemplate;
        try {
            compiledTemplate = compileTemplate(templateFile, templateVars);
        } catch (Exception e) {
            log.error("{} Got exception while compiling message template. Exception: {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }

        //send it
        try {
            telegramBot.execute(telegramBot.createSendMessage(compiledTemplate));
        } catch (TelegramApiException e) {
            log.error("{} failed to send message to Telegram. Got {}: {}",
                    TAG, TelegramApiException.class.getSimpleName(), e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
        return OperationResult.success();
    }

    private OperationResult shortifyLink(final Map<String, Object> vars, final TokenType tokenType) {
        String longLink = (String) vars.getOrDefault("link", App.NO_VALUE);
        if (longLink.equals(App.NO_VALUE)) {
            return OperationResult.elementNotFound().withMessage(MSG_NO_LONG_LINK_FOUND);
        }
        return linkService.shortifyLinkForTokens(longLink, tokenType);
    }

    private OperationResult tokenOwnerHasConfirmedTelegramAccount(final User tokenOwner) {
        if (tokenOwner == null) {
            log.warn("{} Token owner is NULL", TAG);
            return OperationResult.malformedInput().withMessage(ERR_TOKEN_HAS_OWNER);
        }
        Optional<Account> telegramAccount = accountService.getAccount(tokenOwner, AccountType.TELEGRAM);
        if (telegramAccount.isEmpty()) {
            log.warn("{} {}", TAG, ERR_NO_TELEGRAM_ACCOUNT);
            return OperationResult.elementNotFound().withMessage(ERR_NO_TELEGRAM_ACCOUNT);
        }
        return telegramAccount.get().isConfirmed() ? OperationResult.success() : OperationResult.banned();
    }

    private TelegramMessage getTelegramMessage(final Token token, final String telegramAccount) {
        return switch (token.getTokenType()) {
            case ACCOUNT_CONFIRMATION_TOKEN, TELEGRAM_CONFIRMATION_TOKEN, USER_API_TOKEN -> null;
            case LOGIN_VERIFICATION_TOKEN -> new LoginVerificationMessage(token);
            case PASSWORD_RESET_TOKEN -> new PasswordResetMessage(token, telegramAccount);
        };
    }

    private String compileTemplate(final String templateFile, final Map<String, Object> templateVars)
            throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        configuration.getTemplate(templateFile).process(templateVars, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
