package eu.yals.services.telegram;

import com.vdurmont.emoji.EmojiParser;
import eu.yals.constants.App;
import eu.yals.constants.App.Emoji;
import eu.yals.core.IdentGenerator;
import eu.yals.models.Link;
import eu.yals.models.dao.LinkRepo;
import eu.yals.telegram.TelegramBot;
import eu.yals.telegram.TelegramObject;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static eu.yals.constants.App.AT;
import static eu.yals.constants.App.NO_VALUE;

/**
 * Service for {@link TelegramBot}
 *
 * @since 2.4
 */
@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {
    public static final String NO_INIT = "Didn't correctly initialized. Did you run telegramService.init()?";

    private final LinkRepo linkRepo;
    private final AppUtils appUtils;


    private boolean isInitDone = false;
    private TelegramObject telegramObject;

    public TelegramServiceImpl(LinkRepo linkRepo, AppUtils appUtils) {
        this.linkRepo = linkRepo;
        this.appUtils = appUtils;
    }

    @Override
    public void init(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        isInitDone = true;
    }

    @Override
    public Link storeLink(String longUrl) {
        String ident;
        try {
            do {
                ident = IdentGenerator.generateNewIdent();
            } while (linkRepo.findSingleByIdent(ident).isPresent());
        } catch (Exception e) {
            return null;
        }

        Link link = Link.create(ident, longUrl);
        Link savedLink;
        try {
            savedLink = linkRepo.save(link);
        } catch (Exception e) {
            log.error("Got exception while saving new Link " + link.toString(), e);
            savedLink = null;
        }
        return savedLink;
    }

    @Override
    public String success(Link savedLink) {
        if (!isInitDone) {
            return NO_INIT;
        }
        String serverHostname = appUtils.getServerUrl();
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
