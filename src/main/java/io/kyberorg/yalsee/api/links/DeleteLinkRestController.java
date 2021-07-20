package io.kyberorg.yalsee.api.links;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.ApiUtils;
import io.kyberorg.yalsee.utils.TokenChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_404;

/**
 * Deletes malware links.
 *
 * @since 3.0.4
 */
@Slf4j
@RestController
public class DeleteLinkRestController {
    private static final String TAG = "[" + DeleteLinkRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final TokenChecker tokenChecker;
    private final IdentValidator identValidator;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkService service for deleting links
     * @param tokenChecker for checking token
     * @param identValidator for validating ident param
     */
    public DeleteLinkRestController(final LinkService linkService, final TokenChecker tokenChecker,
                                    final IdentValidator identValidator) {
        this.linkService = linkService;
        this.tokenChecker = tokenChecker;
        this.identValidator = identValidator;
    }

    /**
     * Delete Link API Endpoint.
     *
     * DELETE /api/links/{ident}
     *
     * @param ident string with valid ident. Link with given ident should be already stored in system
     * @param request object which contains HTTP request. Needed for extracting request headers
     * @return if deletion successfully done - 204 without body, {@link YalseeErrorJson} with error else.
     */
    @DeleteMapping(value = Endpoint.Api.DELETE_LINKS_API,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> deleteLink(final @PathVariable("ident") String ident,
                                                 final HttpServletRequest request) {
        log.info("{} got request DELETE request: {\"Ident\": {}}", TAG, ident);

        //token check
        OperationResult tokenCheck = tokenChecker.check(request);
        if (tokenCheck.notOk()) {
            return ApiUtils.handleTokenFail(tokenCheck);
        }

        //ident check
        OperationResult identCheck = identValidator.validate(ident);
        if (identCheck.notOk()) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            return ApiUtils.handleIdentFail(identCheck);
        }

        //action
        OperationResult deleteResult = linkService.deleteLinkWithIdent(ident);
        if (deleteResult.ok()) {
            return ResponseEntity.noContent().build();
        } else {
            return handleDeleteOperationFail(deleteResult);
        }
    }

    private ResponseEntity<YalseeErrorJson> handleDeleteOperationFail(final OperationResult result) {
        String operationResult = result.getResult();
        switch (operationResult) {
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
