package io.kyberorg.yalsee.controllers.rest;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.result.DeleteResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RestController
public class DeleteRestController {
    private static final String TAG = "[" + DeleteRestController.class.getSimpleName() + "]";

    private final LinkService linkService;
    private final AppUtils appUtils;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkService service for deleting links
     * @param appUtils utils for getting valid delete token
     */
    public DeleteRestController(final LinkService linkService, final AppUtils appUtils) {
        this.linkService = linkService;
        this.appUtils = appUtils;
    }

    /**
     * Delete Link API Endpoint.
     *
     * @param ident string with valid ident. Link with given ident should be already stored in system
     * @param request object which contains HTTP request. Needed for extracting request headers
     * @return if deletion successfully done - 204 without body, {@link YalseeErrorJson} with error else.
     */
    @DeleteMapping(Endpoint.Api.DELETE_LINK_API + "/{ident}")
    public ResponseEntity<YalseeJson> deleteLink(final @PathVariable("ident") String ident,
                                                 final HttpServletRequest request) {
        log.info("{} got request: {\"Ident\": {}}", TAG, ident);

        //ident check
        if (StringUtils.isBlank(ident)) {
            log.info("{} Got empty ident", TAG);
            YalseeErrorJson payload = YalseeErrorJson.createWithMessage("Request should be like this: "
                    + Endpoint.Api.DELETE_LINK_API + "/{ident}"
                    + " and ident should not be empty").andStatus(HttpCode.STATUS_400);
            return ResponseEntity.badRequest().body(payload);
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            YalseeErrorJson payload = YalseeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string")
                    .andStatus(HttpCode.STATUS_400);
            return ResponseEntity.badRequest().body(payload);
        }

        //token check
        String deleteToken = request.getHeader(Header.X_YALSEE_TOKEN);
        if (StringUtils.isNotBlank(deleteToken)) {
            if (!deleteToken.equals(appUtils.getDeleteToken())) {
                YalseeErrorJson payload = YalseeErrorJson.createWithMessage("Unauthorized: Wrong Delete Token")
                        .andStatus(HttpCode.STATUS_401);
                return ResponseEntity.status(HttpCode.STATUS_401).body(payload);
            }
        } else {
            YalseeErrorJson payload = YalseeErrorJson.createWithMessage("Unauthorized: Delete Token must be present")
                    .andStatus(HttpCode.STATUS_401);
            return ResponseEntity.status(HttpCode.STATUS_401).body(payload);
        }

        //action
        DeleteResult result = linkService.deleteLink(ident);
        if (result instanceof DeleteResult.Success) {
            return ResponseEntity.noContent().build();
        } else if (result instanceof DeleteResult.NotFound) {
            YalseeErrorJson payload = YalseeErrorJson.createWithMessage(((DeleteResult.NotFound) result)
                    .getErrorMessage())
                    .andStatus(HttpCode.STATUS_404);
            return ResponseEntity.status(HttpCode.STATUS_404).body(payload);
        } else if (result instanceof DeleteResult.DatabaseDown) {
            YalseeErrorJson payload =  YalseeErrorJson.createWithMessage(((DeleteResult.DatabaseDown) result)
                    .getErrorMessage())
                    .andStatus(HttpCode.STATUS_503);
            return ResponseEntity.status(HttpCode.STATUS_503).body(payload);
        } else if (result instanceof DeleteResult.Fail) {
            log.error(((DeleteResult.Fail) result).getException().getMessage());
            YalseeErrorJson payload =  YalseeErrorJson.createWithMessage("Unexpected failure")
                    .andStatus(HttpCode.STATUS_500);
            return ResponseEntity.status(HttpCode.STATUS_500).body(payload);
        } else {
            YalseeErrorJson payload =  YalseeErrorJson.createWithMessage("Unexpected failure")
                    .andStatus(HttpCode.STATUS_500);
            return ResponseEntity.status(HttpCode.STATUS_500).body(payload);
        }
    }
}
