package io.kyberorg.yalsee.api.mm;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.internal.LinkServiceInput;
import io.kyberorg.yalsee.json.MattermostResponse;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.mm.Mattermost;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import lombok.RequiredArgsConstructor;
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
    public YalseeJson mm(final @RequestBody String body, final HttpServletRequest req) {
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
        String fullYalseeLink = serverHostname + "/" + savedLink.getIdent();

        String linkDescription = mattermost.getArgumentSet().getDescription();
        if (StringUtils.isBlank(linkDescription)) {
            String userGreet = StringUtils.isNotBlank(mattermost.getUsername())
                    && (!mattermost.getUsername().equals(App.NO_VALUE))
                    ? "Okay " + App.AT + mattermost.getUsername() + ", " : "Okay, ";
            String greeting = userGreet + "here is your short link: ";

            return MattermostResponse.createWithText(greeting + fullYalseeLink);
        } else {
            return MattermostResponse.createWithText(fullYalseeLink + " " + linkDescription);
        }
    }

    private MattermostResponse usage() {
        String command = (Objects.nonNull(mattermost) && StringUtils.isNotBlank(mattermost.getCommand()))
                ? mattermost.getCommand() : "/yalsee";

        return MattermostResponse.createWithText(App.Emoji.INFO + "  Usage: " + command
                + " https://mysuperlonglink.tld [Optional Link Description]");
    }

    private MattermostResponse serverError() {
        return MattermostResponse.createWithText(App.Emoji.WARNING + " Server Error")
                .addGotoLocation(App.Mattermost.SUPPORT_URL);
    }

    private String getServerHostname(final HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString();
        return requestUrl.replace(Endpoint.Api.MM_API, "");
    }
}
