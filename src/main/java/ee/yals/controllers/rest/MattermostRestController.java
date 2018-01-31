package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.MattermostResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.mm.Mattermost;
import ee.yals.mm.Mattermost.Emoji;
import ee.yals.models.Link;
import ee.yals.services.mm.MattermostService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static ee.yals.mm.Mattermost.Constants.AT;

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

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletRequest request) {
        try {
            mattermost = Mattermost.createFromResponseBody(body);
            if (UrlValidator.getInstance().isValid(mattermost.getText())) {
                Link savedLink = mmService.storeLink(mattermost.getText());
                if (Objects.nonNull(savedLink)) {
                    return success(savedLink);
                } else {
                    LOG.error("Was unable to save link. Service returned NULL. Body: " + body);
                    return serverError();
                }
            } else {
                return usage();
            }
        } catch (Exception e) {
            LOG.error("Got exception while handling MM request. Body: " + body + " Exception: ", e);
            return serverError();
        }
    }

    private MattermostResponseJson success(Link savedLink) {
        String hostname = "https://yals.yadev.ee";
        return MattermostResponseJson.createWithText("Okay " + AT + mattermost.getUsername() + ", here is your short link: " + hostname + "/" + savedLink.getIdent());
    }

    private MattermostResponseJson usage() {
        String command = StringUtils.isNotBlank(mattermost.getCommand()) ? mattermost.getCommand() : "/yals";

        return MattermostResponseJson.createWithText(Emoji.WARNING + "  Usage: " + command +
                " http://mysuperlonglink.tld");
    }

    private MattermostResponseJson serverError() {
        return MattermostResponseJson.createWithText(Emoji.WARNING + " Server Error");
    }
}
