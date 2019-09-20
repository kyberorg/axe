package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.json.MattermostResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.mm.Mattermost;
import eu.yals.mm.Mattermost.Emoji;
import eu.yals.models.Link;
import eu.yals.services.mm.MattermostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Objects;

import static eu.yals.mm.Mattermost.Constants.AT;
import static eu.yals.mm.Mattermost.Constants.NO_VALUE;

/**
 * MatterMost chat endpoint
 *
 * @since 2.3
 */
@RestController
@Slf4j
public class MattermostRestController {
    private static final String TAG = "[MM]";

    @Autowired
    private MattermostService mmService;

    private Mattermost mattermost;

    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletRequest request) {
        this.request = request;
        try {
            log.info(String.format("%s Got request from Mattermost. Body: %s", TAG, body));
            log.debug(String.format("%s Parsing MM request", TAG));
            mattermost = Mattermost.createFromResponseBody(body);
            String mmUrl = mattermost.getArgumentSet().getUrl();
            log.debug(String.format("%s Request Parsed. Saving link. mmUrl: %s", TAG, mmUrl));

            Link savedLink = mmService.storeLink(mmUrl);
            if (Objects.nonNull(savedLink)) {
                log.info(String.format("%s Link saved. Replying back", TAG));
                return success(savedLink);
            } else {
                log.error(String.format("%s Was unable to save link. Service returned NULL. Body: %s", TAG, body));
                return serverError();
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error(String.format("%s Got exception while handling request. Body: %s Exception: %s", TAG, body, e));
            return usage();
        } catch (Exception e) {
            log.error(String.format("%s Unknown exception while handling request. Body: %s Exception: %s", TAG, body, e));
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
