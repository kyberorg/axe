package pm.axe.telegram.handlers;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.db.models.Account;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.services.telegram.TelegramService;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.telegram.TelegramCommand;
import pm.axe.telegram.TelegramUserMapping;
import pm.axe.utils.AppUtils;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class HelloHandler implements TelegramCommandHandler {
    private final TelegramService telegramService;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final TelegramUserMapping userMapping;

    private final AppUtils appUtils;

    private static final String HELLO_MESSAGE = Axe.Emoji.HELLO + " Hello";
    private static final String NO_SENSE_MESSAGE = Axe.Emoji.NO_GOOD + " Given string makes no sense to me.";
    private static final String TOKEN_EXPIRED_MESSAGE =
            Axe.Emoji.RUBBISH + " This token may have been used already or it may have expired.";
    private static final String ACC_ALREADY_LINKED = Axe.Emoji.WARNING + " Account already linked with another user."
            + "Do " + TelegramCommand.UNLINK.getCommandText() + " it first.";

    @Override
    public String handle(final Update update) {
        //get message from update
        final Message message = update.getMessage();
        //remove command
        final String tokenString = message.getText().replace(TelegramCommand.HELLO.getCommandText(), "").trim();
        //just hello => hello-hello.
        if (StringUtils.isBlank(tokenString)) {
            final String greeting = String.format("%s%s", Axe.C.AT, update.getMessage().getFrom().getUserName());
            final String getTokenMessage = "Most probably you want to link your account with your Axe user. "
                     + "For this, you will need token. Get one at Profile page.";

            return EmojiParser.parseToUnicode(String.format("%s %s! %s %s",
                    HELLO_MESSAGE, greeting, Axe.C.NEW_LINE, getTokenMessage));
        }
        //check token format aka isToken ? -> 400 (mean-less string)
        if (tokenString.length() != Token.TELEGRAM_TOKEN_LEN) {
            return EmojiParser.parseToUnicode(NO_SENSE_MESSAGE);
        }
        //searching for token aka Token found ? -> 404 (token is already used or never existed)
        Optional<Token> token = tokenService.getToken(tokenString);
        if (token.isEmpty()) {
            return EmojiParser.parseToUnicode(TOKEN_EXPIRED_MESSAGE);
        }
        //token has username? -> 500 (sys err)
        final User axeUser = token.get().getUser();
        final String tgUser = message.getFrom().getUserName();
        if (axeUser == null) {
            return telegramService.serverError();
        }
        //tgUser has Axe Acc? -> 409 (already confirmed)
        if (userMapping.getAxeUser(tgUser).isPresent()) {
            return EmojiParser.parseToUnicode(ACC_ALREADY_LINKED);
        }
        //create new Account -> 500 (failed, write to @kyberorg)
        long chatId = message.getChatId();
        OperationResult createAccountResult = accountService.createTelegramAccount(axeUser, tgUser, chatId);
        if (createAccountResult.notOk()) {
            //AxeUser has tgAcc ? -> 409 (already confirmed)
            if (Objects.equals(createAccountResult.getResult(), OperationResult.CONFLICT)) {
                return EmojiParser.parseToUnicode(ACC_ALREADY_LINKED);
            } else {
                return EmojiParser.parseToUnicode(String.format("%s Failed to link account. Please write to %s",
                        Axe.Emoji.WARNING, Axe.Telegram.KYBERORG));
            }
        }

        //confirm account
        confirmAccount(createAccountResult);
        //create mapping
        createUserMapping(tgUser, axeUser);
        //Delete token (async operation)
        tokenService.deleteTokenRecord(token.get());

        //200 (Congrats, account linked)
        return EmojiParser.parseToUnicode(String.format("%s Great success! "
                        + "Accounts are linked. "
                        + "Since now you can see all links saved with this bot at %s web interface.",
                Axe.Emoji.TADA, StringUtils.capitalize(appUtils.getServerDomain().toLowerCase())));
    }

    /**
     * Triggers {@link Account} confirmation.
     *
     * @param createAccountResult {@link OperationResult} to get {@link Account} from.
     */
    @Async
    public void confirmAccount(final OperationResult createAccountResult) {
        accountService.confirmAccount(createAccountResult.getPayload(Account.class));
    }

    /**
     * Creates new {@link TelegramUserMapping}.
     *
     * @param tgUser non-empty string with Telegram User.
     * @param axeUser corresponding Axe {@link User}
     */
    @Async
    public void createUserMapping(final String tgUser, final User axeUser) {
        userMapping.createMapping(tgUser, axeUser);
    }


}
