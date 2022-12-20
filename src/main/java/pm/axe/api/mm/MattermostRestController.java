package pm.axe.api.mm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.db.models.Link;
import pm.axe.internal.LinkServiceInput;
import pm.axe.json.AxeJson;
import pm.axe.json.MattermostResponse;
import pm.axe.mm.Mattermost;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * MatterMost chat endpoint.
 *
 * @since 2.3
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class MattermostRestController {
    private static final String TAG = "[" + MattermostRestController.class.getSimpleName() + "]";

    private final LinkService linkService;

    private Mattermost mattermost;

    private HttpServletRequest request;

    /**
     * Mattermost API endpoint.
     *
     * @param body body of HTTP request
     * @param req  raw HTTP request
     * @return json given in response
     */
    @RequestMapping(method = RequestMethod.POST, value = Endpoint.Api.MM_API)
    public AxeJson mm(final @RequestBody String body, final HttpServletRequest req) {
        this.request = req;
        try {
            log.info("{} Got request from Mattermost. Body: {}", TAG, body);
            log.debug("{} Parsing MM request", TAG);
            mattermost = Mattermost.createFromResponseBody(body);
            String mmUrl = mattermost.getArgumentSet().getUrl();
            log.debug("{} Request Parsed. Saving link. mmUrl: {}", TAG, mmUrl);

            OperationResult storeResult = linkService.createLink(LinkServiceInput.builder(mmUrl).build());
            if (storeResult.ok()) {
                log.info("{} Link saved. Replying back", TAG);
                return success(storeResult.getPayload(Link.class));
            } else {
                log.error("{} Was unable to save link. Service returned error: {}. Body: {}",
                        TAG, storeResult.getMessage(), body);
                return serverError();
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error("{} Got exception while handling request. Body: {} Exception: {}", TAG, body, e.getMessage());
            log.debug("", e);
            return usage();
        } catch (Exception e) {
            log.error("{} Unknown exception while handling request. Body: {} Exception: {}", TAG, body, e.getMessage());
            log.debug("", e);
            return serverError();
        }
    }

    private MattermostResponse success(final Link savedLink) {
        String serverHostname = getServerHostname(request);
        String fullAxeLink = serverHostname + "/" + savedLink.getIdent();

        String linkDescription = mattermost.getArgumentSet().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = StringUtils.isNotBlank(mattermost.getUsername())
                    && (!mattermost.getUsername().equals(Axe.C.NO_VALUE))
                    ? "Okay " + Axe.C.AT + mattermost.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";

            return MattermostResponse.createWithText(greeting + fullAxeLink);
        } else {
            return MattermostResponse.createWithText(fullAxeLink + " " + linkDescription);
        }
    }

    private MattermostResponse usage() {
        String command = (Objects.nonNull(mattermost) && StringUtils.isNotBlank(mattermost.getCommand()))
                ? mattermost.getCommand() : "/axe";

        return MattermostResponse.createWithText(Axe.Emoji.INFO + "  Usage: " + command
                + " https://mysuperlonglink.tld [Optional Link Description]");
    }

    private MattermostResponse serverError() {
        return MattermostResponse.createWithText(Axe.Emoji.WARNING + " Server Error")
                .addGotoLocation(Axe.Mattermost.SUPPORT_URL);
    }

    private String getServerHostname(final HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString();
        return requestUrl.replace(Endpoint.Api.MM_API, "");
    }
}
