package pm.axe.api.links;

import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import pm.axe.db.models.Link;
import pm.axe.internal.LinkServiceInput;
import pm.axe.json.AxeErrorJson;
import pm.axe.json.PostLinkRequest;
import pm.axe.json.PostLinkResponse;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.utils.ApiUtils;

import java.net.URI;

/**
 * Stores new link.
 *
 * @since 3.1
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PostLinkRestController {
    private static final String TAG = "[" + PostLinkRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final TokenCheckerMiddleware tokenChecker;

    /**
     * API that stores new links.
     *
     * @param requestJson {@link PostLinkRequest} JSON with link to save
     * @param request {@link HttpServletRequest} object to retrieve headers from. Populated by Spring
     * @return {@link ResponseEntity} with {@link PostLinkResponse} and resource URI in {@link Axe.Headers#LOCATION}
     *  or {@link AxeErrorJson}.
     */
    @PostMapping(value = Endpoint.Api.LINKS_API, consumes = MimeTypes.JSON, produces = MimeTypes.JSON)
    public ResponseEntity<?> storeNewLink(final @RequestBody(required = false) PostLinkRequest requestJson,
                                          final HttpServletRequest request) {
        log.info("{} got POST request: {\"JSON\": {}}", TAG, requestJson);

        if (requestJson == null) {
            AxeErrorJson errorJson = AxeErrorJson.createWithMessage("Body should be a JSON object")
                    .andStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(errorJson);
        }

        if (StringUtils.isBlank(requestJson.getLink())) {
            return handleMalformedUrl();
        }

        OperationResult storeResult;
        //token check (ident logic stub: will be changed once users are implemented)
        OperationResult tokenCheck = tokenChecker.checkMasterToken(request);
        if (tokenCheck.ok()) {
            //accepting ident if any
            if (StringUtils.isNotBlank(requestJson.getIdent())) {
                storeResult = linkService.createLink(
                        LinkServiceInput.builder(requestJson.getLink()).customIdent(requestJson.getIdent()).build()
                );
            } else {
                return handleMalformedIdent();
            }
        } else if (tokenCheck.getMessage().equals(TokenCheckerMiddleware.WRONG_TOKEN)) {
            return ApiUtils.handleTokenFail(tokenCheck);
        } else {
            //used-defined ident discarded
            storeResult = linkService.createLink(LinkServiceInput.builder(requestJson.getLink()).build());
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
                AxeErrorJson errorJson = AxeErrorJson.
                        createWithMessage("We already have link stored with given ident. Try another one")
                        .andStatus(HttpStatus.CONFLICT);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorJson);
            case OperationResult.BANNED:
                //analyse ban reason and send 403
                String banReason = storeResult.getMessage();
                AxeErrorJson errorJson1 = AxeErrorJson.createWithMessage(banReason).andStatus(HttpStatus.FORBIDDEN);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorJson1);
            case OperationResult.SYSTEM_DOWN:
                return ApiUtils.handleSystemDown();
            case OperationResult.GENERAL_FAIL:
            default:
                return ApiUtils.handleServerError();
        }
    }

    private ResponseEntity<AxeErrorJson> handleMalformedUrl() {
        AxeErrorJson errorJson =
                AxeErrorJson.createWithMessage("Got malformed value at 'link' field. Should be valid URL")
                        .andStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }

    private ResponseEntity<AxeErrorJson> handleMalformedUrl(final OperationResult operationResult) {
        AxeErrorJson errorJson =
                AxeErrorJson.createWithMessage(operationResult.getMessage())
                        .andStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }

    private ResponseEntity<AxeErrorJson> handleMalformedIdent() {
        AxeErrorJson errorJson =
                AxeErrorJson.createWithMessage("Got malformed 'ident'. It should be from 2 to 255 chars.")
                        .andStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.unprocessableEntity().body(errorJson);
    }
}
