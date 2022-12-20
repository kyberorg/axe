package pm.axe.api.links;

import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pm.axe.Endpoint;
import pm.axe.core.IdentValidator;
import pm.axe.json.AxeErrorJson;
import pm.axe.json.GetLinkResponse;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.utils.ApiUtils;
import pm.axe.utils.AppUtils;
import pm.axe.utils.UrlUtils;

/**
 * Gets long links.
 *
 * @since 3.1
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class GetLinkRestController {
    private static final String TAG = "[" + GetLinkRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final IdentValidator identValidator;

    /**
     * Wildcard API. Currently, not implemented. Reserved to provide user's links.
     *
     * @return currently {@link ResponseEntity} with {@link AxeErrorJson} and {@link HttpStatus#NOT_IMPLEMENTED}
     */
    @GetMapping(path = {Endpoint.Api.LINKS_API, Endpoint.Api.LINKS_API + "/"})
    public ResponseEntity<?> getLinks() {
        AxeErrorJson errorJson = AxeErrorJson.createWithMessage("Not implemented yet")
                .andStatus(HttpStatus.NOT_IMPLEMENTED);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorJson);
    }

    /**
     * Gets long link stored under given ident.
     *
     * @param ident part of link, that identify short link
     * @return {@link ResponseEntity} with {@link GetLinkResponse} or {@link AxeErrorJson}
     */
    @GetMapping(value = Endpoint.Api.GET_LINKS_API,
            produces = MimeTypes.JSON)
    public ResponseEntity<?> getLink(final @PathVariable("ident") String ident) {
        log.info("{} got GET request: {\"Ident\": {}}", TAG, ident);

        //ident check
        OperationResult identCheck = identValidator.validate(ident);
        if (identCheck.notOk()) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            return ApiUtils.handleIdentFail(identCheck);
        }

        //action
        OperationResult opResult = linkService.getLinkWithIdent(ident);
        if (opResult.ok()) {
            //return 200 + Link
            String longLink = opResult.getStringPayload();
            log.info("{} Hit. {\"Ident\": {}, \"Link found\": {}}", TAG, ident, longLink);
            if (!AppUtils.isAscii(longLink)) {
                // Handle international domains by detecting non-ascii and converting them to punycode
                String punycodedUrl = UrlUtils.covertUnicodeUrlToAscii(longLink);
                log.info("{} encoding URL using punycode. Link: {} transformed to {}", TAG, longLink, punycodedUrl);
                longLink = punycodedUrl;
            }
            GetLinkResponse responseJson = GetLinkResponse.create().withLink(longLink);
            return ResponseEntity.ok(responseJson);
        } else {
            return handleGetOperationFail(opResult);
        }
    }

    private ResponseEntity<AxeErrorJson> handleGetOperationFail(final OperationResult result) {
        switch (result.getResult()) {
            case OperationResult.ELEMENT_NOT_FOUND -> {
                log.info("{} ident not found", TAG);
                AxeErrorJson errorJson = AxeErrorJson.createWithMessage("No link with given ident stored")
                        .andStatus(HttpStatus.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorJson);
            }
            case OperationResult.SYSTEM_DOWN -> {
                log.error("{} Database is DOWN", TAG);
                return ApiUtils.handleSystemDown();
            }
            default -> {
                log.error("{} Error: {}", TAG, result.getMessage());
                return ApiUtils.handleServerError();
            }
        }
    }
}
