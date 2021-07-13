package io.kyberorg.yalsee.api.links;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.json.LinkResponseJson;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.ApiUtils;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_404;
import static io.kyberorg.yalsee.constants.HttpCode.STATUS_501;

/**
 * Gets long links.
 *
 * @since 3.1
 */
@Slf4j
@RestController
public class GetLinkRestController {
    private static final String TAG = "[" + GetLinkRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final IdentValidator identValidator;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkService service for getting links
     * @param identValidator for validating ident param
     */
    public GetLinkRestController(final LinkService linkService, final IdentValidator identValidator) {
        this.linkService = linkService;
        this.identValidator = identValidator;
    }

    @GetMapping(path = {Endpoint.Api.LINKS_API, Endpoint.Api.LINKS_API + "/"})
    public ResponseEntity<?> getLinks() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Not implemented yet")
                .andStatus(STATUS_501);
        return ResponseEntity.status(STATUS_501).body(errorJson);
    }

    @GetMapping(Endpoint.Api.GET_LINKS_API)
    public ResponseEntity<?> getLink(final @PathVariable("ident") String ident, final HttpServletRequest request) {
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
                String punycodedUrl = AppUtils.covertUnicodeToAscii(longLink);
                log.info("{} encoding URL using punycode. Link: {} transformed to {}", TAG, longLink, punycodedUrl);
                longLink = punycodedUrl;
            }
            LinkResponseJson responseJson = LinkResponseJson.create().withLink(longLink);
            return ResponseEntity.ok(responseJson);
        } else {
            return handleGetOperationFail(opResult);
        }
    }

    private ResponseEntity<YalseeErrorJson> handleGetOperationFail(final OperationResult result) {
        switch (result.getResult()) {
            case OperationResult.ELEMENT_NOT_FOUND:
                log.info("{} ident not found", TAG);
                YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("No link with given ident stored")
                        .andStatus(STATUS_404);
                return ResponseEntity.status(STATUS_404).body(errorJson);
            case OperationResult.SYSTEM_DOWN:
                log.error("{} Database is DOWN", TAG);
                return ApiUtils.handleSystemDown();
            case OperationResult.GENERAL_FAIL:
            default:
                log.error("{} Error: {}", TAG, result.getMessage());
                return ApiUtils.handleServerError();
        }
    }
}
