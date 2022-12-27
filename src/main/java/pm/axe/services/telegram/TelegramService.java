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
    public static long WRONG_CHAT_ID = -1L;
    private final AppUtils appUtils;

    /**
     * Performs actions after longURL was successfully shortened and stored into DB.
     *
     * @param tgUser  {@link String} with telegram username
     * @param savedLink {@link Link} object which corresponds with saved record
     * @param linkDescription {@link Optional} string with link description
     * @return message for sending to user
     */
    public String success(final String tgUser, final Link savedLink, final Optional<String> linkDescription) {
        String shortUrl = appUtils.getShortUrl();
        String fullLink = shortUrl + "/" + savedLink.getIdent();

        if (linkDescription.isEmpty()) {
            final String userGreet = String.format("Okay, %s%s", Axe.C.AT, tgUser);
            return String.format("%s here is your short link: %s", userGreet, fullLink);
        } else {
            return fullLink + " " + linkDescription.get();
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
                + "/usage - Show this message";

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

    public Optional<Message> getTelegramMessage(final Update update) {
        if (update.hasMessage()) {
            return Optional.of(update.getMessage());
        } else if (update.hasEditedMessage()) {
            return Optional.of(update.getEditedMessage());
        } else {
            return Optional.empty();
        }
    }

    public TelegramCommand extractCommand(final Message message) {
        String[] args = message.getText().split(" ");
        if (args.length == 0) {
            return TelegramCommand.NOT_A_COMMAND;
        } else {
            return TelegramCommand.createFromString(args[0]);
        }
    }

    public long getChatId(final Update update) {
        if (update == null || update.getMessage() == null) {
            return WRONG_CHAT_ID;
        } else {
            return update.getMessage().getChatId();
        }
    }
}
