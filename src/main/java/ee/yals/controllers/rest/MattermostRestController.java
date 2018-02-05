package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.MattermostResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.mm.Mattermost;
import ee.yals.mm.Mattermost.Emoji;
import ee.yals.models.Link;
import ee.yals.services.mm.MattermostService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Objects;

import static ee.yals.mm.Mattermost.Constants.AT;
import static ee.yals.mm.Mattermost.Constants.NO_VALUE;

/**
 * MatterMost chat endpoint
 *
 * @since 2.3
 */
@RestController
public class MattermostRestController {

    private static final Logger LOG = Logger.getLogger(MattermostRestController.class);

    @Autowired
    private MattermostService mmService;

    private Mattermost mattermost;

    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletRequest request) {
        this.request = request;
        try {
            LOG.debug("Body: " + body);
            mattermost = Mattermost.createFromResponseBody(body);
            String mmUrl = mattermost.getArgumentSet().getUrl();

            Link savedLink = mmService.storeLink(mmUrl);
            if (Objects.nonNull(savedLink)) {
                return success(savedLink);
            } else {
                LOG.error("Was unable to save link. Service returned NULL. Body: " + body);
                return serverError();
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            LOG.error("Got exception while handling MM request. Body: " + body + " Exception: ", e);
            return usage();
        } catch (Exception e) {
            LOG.error("Got exception while handling MM request. Body: " + body + " Exception: ", e);
            return serverError();
        }
    }

    private MattermostResponseJson success(Link savedLink) {
        String serverHostname = getServerHostname(request);
        String fullYalsLink = serverHostname + "/" + savedLink.getIdent();

        String linkDescription = mattermost.getArgumentSet().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = StringUtils.isNotBlank(mattermost.getUsername()) && (!mattermost.getUsername().equals(NO_VALUE)) ?
                    "Okay " + AT + mattermost.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";

            return MattermostResponseJson.createWithText(greeting + fullYalsLink);
        } else {
            return MattermostResponseJson.createWithText(fullYalsLink + " " + linkDescription);
        }
    }

    private MattermostResponseJson usage() {
        String command = (Objects.nonNull(mattermost) && StringUtils.isNotBlank(mattermost.getCommand())) ?
                mattermost.getCommand() : "/yals";

        return MattermostResponseJson.createWithText(Emoji.INFO + "  Usage: " + command +
                " http://mysuperlonglink.tld [Optional Link Description]");
    }

    private MattermostResponseJson serverError() {
        return MattermostResponseJson.createWithText(Emoji.WARNING + " Server Error")
                .addGotoLocation(Mattermost.Constants.SUPPORT_URL);
    }

    private String getServerHostname(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        return requestUrl.replace(Endpoint.MM_API, "");
    }
}
