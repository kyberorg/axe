package ee.yals.services.telegram;

import com.vdurmont.emoji.EmojiParser;
import ee.yals.constants.App;
import ee.yals.constants.App.Emoji;
import ee.yals.core.IdentGenerator;
import ee.yals.models.Link;
import ee.yals.models.dao.LinkRepo;
import ee.yals.telegram.TelegramObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ee.yals.constants.App.AT;
import static ee.yals.constants.App.NO_VALUE;

/**
 * Service for {@link ee.yals.telegram.TelegramBot}
 *
 * @since 2.4
 */
@Service
public class TelegramServiceImpl implements TelegramService {
    private static final Logger LOG = Logger.getLogger(TelegramServiceImpl.class);
    public static final String NO_INIT = "Didn't correctly initialized. Did you run telegramService.init()?";

    @Autowired
    private LinkRepo linkRepo;

    private boolean isInitDone = false;
    private TelegramObject telegramObject;

    @Override
    public void init(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        isInitDone = true;
    }

    @Override
    public Link storeLink(String longUrl) {
        String ident;
        do {
            ident = IdentGenerator.generateNewIdent();
        } while (linkRepo.findSingleByIdent(ident).isPresent());

        Link link = Link.create(ident, longUrl);
        Link savedLink;
        try {
            savedLink = linkRepo.save(link);
        } catch (Exception e) {
            LOG.error("Got exception while saving new Link " + link.toString(), e);
            savedLink = null;
        }
        return savedLink;
    }

    @Override
    public String success(Link savedLink) {
        if (!isInitDone) {
            return "Didn't correctly inited";
        }
        //String serverHostname = getServerHostname(request);
        String serverHostname = "https://yals.ee"; //FIXME
        String fullYalsLink = serverHostname + "/" + savedLink.getIdent();

        String linkDescription = telegramObject.getArguments().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = (StringUtils.isNotBlank(telegramObject.getUsername()) && (!telegramObject.getUsername().equals(NO_VALUE))) ?
                    "Okay " + AT + telegramObject.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";
            return greeting + fullYalsLink;
        } else {
            return fullYalsLink + " " + linkDescription;
        }
    }

    @Override
    public String usage() {
        if (!isInitDone) {
            return NO_INIT;
        }

        String message = " This bot makes short links from long ones" +
                App.NEW_LINE + App.NEW_LINE +
                "https://mySuperLongLink.com" +
                App.NEW_LINE + App.NEW_LINE +
                "or" + 
                App.NEW_LINE + App.NEW_LINE +
                "https://mySuperLongLink.com description" +
                App.NEW_LINE + App.NEW_LINE +
                "/usage - Show this message";

        return EmojiParser.parseToUnicode(Emoji.INFO + message);
    }

    @Override
    public String serverError() {
        if (!isInitDone) {
            return NO_INIT;
        }
        return EmojiParser.parseToUnicode(Emoji.WARNING + " serverError");
    }
}
