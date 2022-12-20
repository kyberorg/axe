package pm.axe.services.telegram;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pm.axe.Axe;
import pm.axe.db.models.Link;
import pm.axe.telegram.TelegramBot;
import pm.axe.telegram.TelegramObject;
import pm.axe.utils.AppUtils;

/**
 * Service for {@link TelegramBot}.
 *
 * @since 2.4
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramService {
    public static final String TAG = "[" + TelegramService.class.getSimpleName() + "]";
    public static final String NO_INIT = "Didn't correctly initialized. Did you run telegramService.init()?";

    private final AppUtils appUtils;

    private boolean isInitDone = false;
    private TelegramObject telegramObject;

    /**
     * Initialize telegram with {@link TelegramObject}.
     *
     * @param telegramObject valid {@link TelegramObject}
     */
    public void init(final TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        isInitDone = true;
    }

    /**
     * Performs actions after longURL was successfully shortened and stored into DB.
     *
     * @param savedLink {@link Link} object which corresponds with saved record
     * @return message for sending to user
     */
    public String success(final Link savedLink) {
        if (!isInitDone) {
            return NO_INIT;
        }
        String shortUrl = appUtils.getShortUrl();
        String fullLink = shortUrl + "/" + savedLink.getIdent();

        String linkDescription = telegramObject.getArguments().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = (StringUtils.isNotBlank(telegramObject.getUsername())
                    && (!telegramObject.getUsername().equals(Axe.C.NO_VALUE)))
                    ? "Okay " + Axe.C.AT + telegramObject.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";
            return greeting + fullLink;
        } else {
            return fullLink + " " + linkDescription;
        }
    }

    /**
     * Builds usage message.
     *
     * @return usage message to sent to user
     */
    public String usage() {
        if (!isInitDone) {
            return NO_INIT;
        }

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
        if (!isInitDone) {
            return NO_INIT;
        }
        return EmojiParser.parseToUnicode(Axe.Emoji.WARNING + " serverError");
    }
}
