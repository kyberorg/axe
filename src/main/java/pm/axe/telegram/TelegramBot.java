package pm.axe.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.db.models.Account;
import pm.axe.db.models.Link;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.internal.LinkServiceInput;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.services.telegram.TelegramService;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.users.AccountType;
import pm.axe.utils.AppUtils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Telegram Bot.
 *
 * @since 2.4
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String TAG = "[" + TelegramBot.class.getSimpleName() + "]";
    private static final Message NO_MESSAGE = new Message();

    private final TelegramService telegramService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    private final TokenService tokenService;
    private final AccountService accountService;

    private Update update;
    private TelegramObject telegramObject;

    @Override
    public void onUpdateReceived(final Update update) {
        if (log.isTraceEnabled()) {
            log.trace(TAG + " New Update " + update);
        }
        this.update = update;

        String message;
        try {
            if (Objects.isNull(telegramService)) {
                throw new IllegalStateException("Internal server error: ");
            }

            log.debug("{} Update (Author: {}, Message: {})", TAG,
                    getMessage().getFrom().getUserName(), getMessage().getText());

            telegramObject = TelegramObject.createFromUpdate(update);

            if (log.isDebugEnabled()) {
                log.debug(TAG + " Debugging " + TelegramObject.class.getSimpleName() + Axe.C.NEW_LINE + telegramObject);
            }
            telegramService.init(telegramObject);

            TelegramCommand telegramCommand = telegramObject.getCommand();
            message = switch (telegramCommand) {
                case AXE -> doLinkAxing();
                case START -> doHello(update);
                case BYE ->  doBye(update);
                default -> telegramService.usage();
            };
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error(TAG + " Got exception while handling incoming update."
                    + " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.usage();
        } catch (IllegalStateException e) {
            message = "Internal error: not all components are available. Bot currently not available";
            log.error(TAG + " " + message + " " + e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error(TAG + " Got unexpected exception while processing telegram update"
                    + " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.serverError();
        }

        if (message.equals(TelegramService.NO_INIT)) {
            log.error(TelegramService.NO_INIT);
            message = "Internal error: not all components are initialized";
        }

        try {
            execute(createSendMessage(message));
        } catch (TelegramApiException e) {
            log.error(TAG + " Failed to send telegram message. Message: " + message
                    + " " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "Axe Bot";
    }

    @Override
    public String getBotToken() {
        return appUtils.getTelegramToken();
    }

    private String doLinkAxing() {
        String message;
        if (telegramObject.getArguments() == TelegramArguments.EMPTY_ARGS) {
            throw new NoSuchElementException("Got " + telegramObject.getCommand()
                    + " command without arguments. Nothing to shorten");
        } else if (telegramObject.getArguments() == TelegramArguments.BROKEN_ARGS) {
            throw new IllegalArgumentException(
                    "UserMessage must contain URL as first or second (when first is command) param");
        }

        String url = telegramObject.getArguments().getUrl();
        log.debug("{} URL received {}", TAG, url);
        OperationResult storeResult = linkService.createLink(LinkServiceInput.builder(url).build());
        if (storeResult.ok()) {
            message = telegramService.success(storeResult.getPayload(Link.class));
        } else {
            message = telegramService.serverError();
        }
        return message;
    }

    private String doHello(final Update update) {
        Optional<Message> telegramMessage = telegramService.getTelegramMessage(update);
        if (telegramMessage.isEmpty()) {
            return telegramService.serverError();
        }
        final Message message = telegramMessage.get();
        if (StringUtils.isBlank(message.getText())) {
            return telegramService.serverError();
        }
        //remove command
        final String tokenString = message.getText().replace(TelegramCommand.START.getCommandText(), "").trim();
        //just start => usage
        if (StringUtils.isBlank(tokenString)) {
            return telegramService.usage();
        }
        //check token format aka isToken ? -> 400 (mean-less string)
        if (tokenString.length() != Token.TELEGRAM_TOKEN_LEN) {
            return "Given string makes no sense.";
        }
        //searching for token aka Token found ? -> 404 (token is already used or never existed)
        Optional<Token> token = tokenService.getToken(tokenString);
        if (token.isEmpty()) {
            return "Token may have been used already or it may have expired.";
        }
        //token has username? -> 500 (sys err)
        final User axeUser = token.get().getUser();
        final String tgUser = message.getFrom().getUserName();
        if (axeUser == null) {
            return telegramService.serverError();
        }
        //AxeUser has tgAcc ? -> 409 (already confirmed)
        Optional<Account> tgAccount = accountService.getAccount(axeUser, AccountType.TELEGRAM);
        if (tgAccount.isPresent()) {
            return "Account already linked.";
        }
        //tgUser has Axe Acc? -> 409 (already confirmed)
        //create new Account -> 500 (failed, write to @kyberorg)
        OperationResult createAccountResult = accountService.createTelegramAccount(axeUser, tgUser);
        if (createAccountResult.notOk()) {
            if (Objects.equals(createAccountResult.getResult(), OperationResult.CONFLICT)) {
                return "Account already linked.";
            } else {
                return "Failed to link account. Please write to " + Axe.Telegram.KYBERORG;
            }
        }
        //delete token
        tokenService.deleteTokenRecord(token.get());
        //200 (Congrats, account linked)
        return "Great success! Account linked. Now you can save links using this bot and see result at "
                + AppUtils.getShortUrlFromStaticContext() + "/" + Endpoint.UI.MY_LINKS_PAGE;
    }

    private String doBye(final Update update) {
        Optional<Message> telegramMessage = telegramService.getTelegramMessage(update);
        if (telegramMessage.isEmpty()) {
            return telegramService.serverError();
        }
        final Message message = telegramMessage.get();
        if (StringUtils.isBlank(message.getText())) {
            return telegramService.serverError();
        }
        final String tgUser = message.getFrom().getUserName();
        Optional<Account> account = accountService.getAccountByAccountName(tgUser, AccountType.TELEGRAM);
        account.ifPresent(accountService::deleteAccount);
        return "Goodbye! Thanks for using me!";
    }

    /**
     * Converts String Message to {@link SendMessage}.
     *
     * @param message string with message to send.
     * @return {@link SendMessage} with given message inside.
     */
    public SendMessage createSendMessage(final String message) {
        Message telegramMessage = getMessage();
        return SendMessage.builder()
                .chatId(Long.toString(telegramMessage.getChatId()))
                .text(message)
                .build();
    }

    private Message getMessage() {
        Message telegramMessage;
        if (update.hasMessage()) {
            telegramMessage = update.getMessage();
        } else if (update.hasEditedMessage()) {
            telegramMessage = update.getEditedMessage();
        } else {
            telegramMessage = NO_MESSAGE;
        }
        return telegramMessage;
    }


}
