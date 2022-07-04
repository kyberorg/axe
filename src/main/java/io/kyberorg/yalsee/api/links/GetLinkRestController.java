package io.kyberorg.yalsee.api.links;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.json.GetLinkResponse;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.ApiUtils;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
     * @return currently {@link ResponseEntity} with {@link YalseeErrorJson} and {@link HttpCode#NOT_IMPLEMENTED}
     */
    @GetMapping(path = {Endpoint.Api.LINKS_API, Endpoint.Api.LINKS_API + "/"})
    public ResponseEntity<?> getLinks() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Not implemented yet")
                .andStatus(HttpCode.NOT_IMPLEMENTED);
        return ResponseEntity.status(HttpCode.NOT_IMPLEMENTED).body(errorJson);
    }

    /**
     * Gets long link stored under given ident.
     *
     * @param ident part of link, that identify short link
     * @return {@link ResponseEntity} with {@link GetLinkResponse} or {@link YalseeErrorJson}
     */
    @GetMapping(value = Endpoint.Api.GET_LINKS_API,
            produces = MimeType.APPLICATION_JSON)
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

    private ResponseEntity<YalseeErrorJson> handleGetOperationFail(final OperationResult result) {
        switch (result.getResult()) {
            case OperationResult.ELEMENT_NOT_FOUND -> {
                log.info("{} ident not found", TAG);
                YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("No link with given ident stored")
                        .andStatus(HttpCode.NOT_FOUND);
                return ResponseEntity.status(HttpCode.NOT_FOUND).body(errorJson);
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
