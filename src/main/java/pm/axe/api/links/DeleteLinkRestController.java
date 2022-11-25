package pm.axe.api.links;

import pm.axe.Endpoint;
import pm.axe.constants.HttpCode;
import pm.axe.constants.MimeType;
import pm.axe.core.IdentValidator;
import pm.axe.json.AxeErrorJson;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.utils.ApiUtils;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Deletes malware links.
 *
 * @since 3.0.4
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DeleteLinkRestController {
    private static final String TAG = "[" + DeleteLinkRestController.class.getSimpleName() + "]";
    private final LinkService linkService;
    private final TokenCheckerMiddleware tokenChecker;
    private final IdentValidator identValidator;

    /**
     * Delete Link API Endpoint.
     *
     * DELETE /api/links/{ident}
     *
     * @param ident string with valid ident. Link with given ident should be already stored in system
     * @param request object which contains HTTP request. Needed for extracting request headers
     * @return if deletion successfully done - 204 without body, {@link AxeErrorJson} with error else.
     */
    @DeleteMapping(value = Endpoint.Api.DELETE_LINKS_API,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> deleteLink(final @PathVariable("ident") String ident,
                                                 final HttpServletRequest request) {
        log.info("{} got request DELETE request: {\"Ident\": {}}", TAG, ident);

        //token check
        OperationResult tokenCheck = tokenChecker.checkMasterToken(request);
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

    private ResponseEntity<AxeErrorJson> handleDeleteOperationFail(final OperationResult result) {
        String operationResult = result.getResult();
        switch (operationResult) {
            case OperationResult.ELEMENT_NOT_FOUND -> {
                log.info("{} ident not found", TAG);
                AxeErrorJson errorJson = AxeErrorJson.createWithMessage("No link with given ident stored")
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
