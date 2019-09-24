package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.json.MattermostResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.mm.Mattermost;
import eu.yals.models.Link;
import eu.yals.services.mm.MattermostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * MatterMost chat endpoint
 *
 * @since 2.3
 */
@Slf4j
@RestController
public class MattermostRestController {
    private static final String TAG = "[MM]";

    private final MattermostService mmService;

    private Mattermost mattermost;

    private HttpServletRequest request;

    public MattermostRestController(MattermostService mmService) {
        this.mmService = mmService;
    }

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletRequest request) {
        this.request = request;
        try {
            log.info("{} Got request from Mattermost. Body: {}", TAG, body);
            log.debug("{} Parsing MM request", TAG);
            mattermost = Mattermost.createFromResponseBody(body);
            String mmUrl = mattermost.getArgumentSet().getUrl();
            log.debug("{} Request Parsed. Saving link. mmUrl: {}", TAG, mmUrl);

            Link savedLink = mmService.storeLink(mmUrl);
            if (Objects.nonNull(savedLink)) {
                log.info("{} Link saved. Replying back", TAG);
                return success(savedLink);
            } else {
                log.error("{} Was unable to save link. Service returned NULL. Body: {}", TAG, body);
                return serverError();
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error("{} Got exception while handling request. Body: {} Exception: {}", TAG, body, e);
            return usage();
        } catch (Exception e) {
            log.error("{} Unknown exception while handling request. Body: {} Exception: {}", TAG, body, e);
            return serverError();
        }
    }

    private MattermostResponseJson success(Link savedLink) {
        String serverHostname = getServerHostname(request);
        String fullYalsLink = serverHostname + "/" + savedLink.getIdent();

        String linkDescription = mattermost.getArgumentSet().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = StringUtils.isNotBlank(mattermost.getUsername()) && (!mattermost.getUsername().equals(App.NO_VALUE)) ?
                    "Okay " + App.AT + mattermost.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";

            return MattermostResponseJson.createWithText(greeting + fullYalsLink);
        } else {
            return MattermostResponseJson.createWithText(fullYalsLink + " " + linkDescription);
        }
    }

    private MattermostResponseJson usage() {
        String command = (Objects.nonNull(mattermost) && StringUtils.isNotBlank(mattermost.getCommand())) ?
                mattermost.getCommand() : "/yals";

        return MattermostResponseJson.createWithText(App.Emoji.INFO + "  Usage: " + command +
                " http://mysuperlonglink.tld [Optional Link Description]");
    }

    private MattermostResponseJson serverError() {
        return MattermostResponseJson.createWithText(App.Emoji.WARNING + " Server Error")
                .addGotoLocation(App.Mattermost.SUPPORT_URL);
    }

    private String getServerHostname(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        return requestUrl.replace(Endpoint.MM_API, "");
    }
}
