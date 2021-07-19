package io.kyberorg.yalsee.api.links;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.json.PostLinkRequest;
import io.kyberorg.yalsee.json.PostLinkResponse;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.ApiUtils;
import io.kyberorg.yalsee.utils.TokenChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static io.kyberorg.yalsee.constants.HttpCode.*;

/**
 * Stores new link.
 *
 * @since 3.1
 */
@Slf4j
@RestController
public class PostLinkRestController {
    private static final String TAG = "[" + PostLinkRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final TokenChecker tokenChecker;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkService service for getting links
     * @param tokenChecker for checking token
     */
    public PostLinkRestController(final LinkService linkService, final TokenChecker tokenChecker) {
        this.linkService = linkService;
        this.tokenChecker = tokenChecker;
    }

    /**
     * API that stores new links.
     *
     * @param storeRequest JSON with link to save
     * @param request request object to retrieve headers from
     * @return {@link ResponseEntity} with {@link PostLinkResponse} and resource URI in {@link Header#LOCATION}
     *  or {@link YalseeErrorJson}.
     */
    @PostMapping(value = Endpoint.Api.LINKS_API,
            consumes = MimeType.APPLICATION_JSON, produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> storeNewLink(final @RequestBody(required = false) PostLinkRequest storeRequest,
                                          final HttpServletRequest request) {
        log.info("{} got POST request: {\"JSON\": {}}", TAG, storeRequest);

        if (storeRequest == null) {
            YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Body should be a JSON object")
                    .andStatus(STATUS_400);
            return ResponseEntity.badRequest().body(errorJson);
        }

        if (StringUtils.isBlank(storeRequest.getLink())) {
            return handleMalformedUrl();
        }

        OperationResult storeResult;
        //token check (ident logic stub: will be changed once users are implemented)
        OperationResult tokenCheck = tokenChecker.check(request);
        if (tokenCheck.ok()) {
            //accepting ident if any
            if (StringUtils.isNotBlank(storeRequest.getIdent())) {
                storeResult = linkService.createLink(storeRequest.getIdent(), storeRequest.getLink());
            } else {
                return handleMalformedIdent();
            }
        } else if (tokenCheck.getMessage().equals(TokenChecker.WRONG_TOKEN)) {
            return ApiUtils.handleTokenFail(tokenCheck);
        } else {
            //used-defined ident discarded
            storeResult = linkService.createLink(storeRequest.getLink());
        }

        switch (storeResult.getResult()) {
            case OperationResult.OK:
                //extract and send 201
                Link storedLink = storeResult.getPayload(Link.class);
                URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                        .path("/{ident}")
                        .buildAndExpand(storedLink.getIdent())
                        .toUri();
                PostLinkResponse responseJson = PostLinkResponse.create().withIdent(storedLink.getIdent());
                return ResponseEntity.created(uri).body(responseJson);
            case OperationResult.MALFORMED_INPUT:
                //analyse message and send 422
                if (storeResult.getMessage().equals(LinkService.OP_MALFORMED_URL)) {
                    return handleMalformedUrl(storeResult);
                } else {
                    return handleMalformedIdent();
                }
            case OperationResult.CONFLICT:
                //sent 409
                YalseeErrorJson errorJson = YalseeErrorJson.
                        createWithMessage("We already have link stored with given ident. Try another one")
                        .andStatus(HttpCode.STATUS_409);
                return ResponseEntity.status(STATUS_409).body(errorJson);
            case OperationResult.BANNED:
                //analyse ban reason and send 403
                String banReason = storeResult.getMessage();
                YalseeErrorJson errorJson1 = YalseeErrorJson.createWithMessage(banReason).andStatus(STATUS_403);
                return ResponseEntity.status(STATUS_403).body(errorJson1);
            case OperationResult.SYSTEM_DOWN:
                return ApiUtils.handleSystemDown();
            case OperationResult.GENERAL_FAIL:
            default:
                return ApiUtils.handleServerError();
        }
    }

    private ResponseEntity<YalseeErrorJson> handleMalformedUrl() {
        YalseeErrorJson errorJson =
                YalseeErrorJson.createWithMessage("Got malformed value at 'link' field. Should be valid URL")
                        .andStatus(STATUS_422);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }

    private ResponseEntity<YalseeErrorJson> handleMalformedUrl(final OperationResult operationResult) {
        YalseeErrorJson errorJson =
                YalseeErrorJson.createWithMessage(operationResult.getMessage())
                        .andStatus(STATUS_422);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }

    private ResponseEntity<YalseeErrorJson> handleMalformedIdent() {
        YalseeErrorJson errorJson =
                YalseeErrorJson.createWithMessage("Got malformed 'ident'. It should be from 2 to 255 chars.")
                        .andStatus(STATUS_422);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }
}
