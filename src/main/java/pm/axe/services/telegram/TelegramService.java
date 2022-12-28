package pm.axe.services.telegram;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.db.models.Link;
import pm.axe.telegram.TelegramBot;
import pm.axe.telegram.TelegramCommand;
import pm.axe.utils.AppUtils;

import java.util.Optional;

/**
 * Service for {@link TelegramBot}.
 *
 * @since 2.4
 */
@RequiredArgsConstructor
@Service
public class TelegramService {
    public static final long WRONG_CHAT_ID = -1L;
    private final AppUtils appUtils;

    /**
     * Performs actions after longURL was successfully shortened and stored into DB.
     *
     * @param savedLink {@link Link} object which corresponds with saved record
     * @param linkDescription {@link Optional} string with link description
     * @return message for sending to user
     */
    public String success(final Link savedLink, final Optional<String> linkDescription) {
        String shortUrl = appUtils.getShortUrl();
        String fullLink = shortUrl + "/" + savedLink.getIdent();

        if (linkDescription.isEmpty()) {
            final String axed = String.format("%s Axed!", Axe.Emoji.AXE);
            return EmojiParser.parseToUnicode(String.format("%s Here is your short link: %s", axed, fullLink));
        } else {
            return EmojiParser.parseToUnicode(Axe.Emoji.AXE + fullLink + " " + linkDescription.get());
        }
    }

    /**
     * Builds usage message.
     *
     * @return usage message to sent to user
     */
    public String usage() {
        String message = " This bot makes short links from long ones"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + "https://mySuperLongLink.com"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + "or"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + "https://mySuperLongLink.com description"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + TelegramCommand.HELLO.getCommandText() + " Token - links this telegram account with Axe user"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + TelegramCommand.MY_AXE_USER.getCommandText() + " - shows Axe user linked to this telegram account"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + TelegramCommand.UNLINK.getCommandText() + " - removes active linking"
                + Axe.C.NEW_LINE + Axe.C.NEW_LINE
                + TelegramCommand.USAGE.getCommandText() + " - Show this message";

        return EmojiParser.parseToUnicode(Axe.Emoji.INFO + message);
    }

    /**
     * Builds server error message.
     *
     * @return string with server error message to send to user
     */
    public String serverError() {
        return EmojiParser.parseToUnicode(Axe.Emoji.WARNING + " serverError");
    }

    /**
     * Extracts {@link Message} from {@link Update}.
     *
     * @param update received Telegram {@link Update}.
     *
     * @return {@link Optional} with {@link Message} or {@link Optional#empty()}.
     * if {@link Update} contains {@link Update#editedMessage}, that {@link Update#editedMessage} will be return.
     */
    public Optional<Message> getTelegramMessage(final Update update) {
        if (update.hasMessage()) {
            return Optional.of(update.getMessage());
        } else if (update.hasEditedMessage()) {
            return Optional.of(update.getEditedMessage());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Parses {@link TelegramCommand} from non-empty {@link Message}.
     *
     * @param message non-empty Telegram {@link Message}.
     * @return parsed {@link TelegramCommand} or {@link TelegramCommand#NOT_A_COMMAND},
     * if {@link Message#getText()} doesn't start with }{@literal /}.
     */
    public TelegramCommand extractCommand(final Message message) {
        String[] args = message.getText().split(" ");
        if (args.length == 0) {
            return TelegramCommand.NOT_A_COMMAND;
        } else {
            return TelegramCommand.createFromString(args[0]);
        }
    }

    /**
     * Gets {@link Message#getChatId()} from {@link Update} object.
     *
     * @param update valid Telegram {@link Update} object.
     * @return parsed {@link Message#getChatId()} or {@link #WRONG_CHAT_ID}.
     */
    public long getChatId(final Update update) {
        if (update == null || update.getMessage() == null) {
            return WRONG_CHAT_ID;
        } else {
            return update.getMessage().getChatId();
        }
    }
}
