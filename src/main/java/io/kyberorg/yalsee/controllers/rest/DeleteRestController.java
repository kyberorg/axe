package io.kyberorg.yalsee.controllers.rest;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.json.EmptyJson;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.result.DeleteResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Deletes malware links
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
     * @param linksService service for deleting links
     * @param appUtils utils for getting valid delete token
     */
    public DeleteRestController(final LinkService linkService, final AppUtils appUtils) {
        this.linkService = linkService;
        this.appUtils = appUtils;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = Endpoint.Api.DELETE_LINK_API + "/{ident}")
    public YalseeJson deleteLink(final @PathVariable("ident") String ident, final HttpServletRequest request,
                                 final HttpServletResponse response) {
        log.info("{} got request: {\"Ident\": {}}", TAG, ident);

        //ident check
        if (StringUtils.isBlank(ident)) {
            log.info("{} Got empty ident", TAG);
            response.setStatus(HttpCode.STATUS_400);
            return YalseeErrorJson.createWithMessage("Request should be like this: "
                    + Endpoint.Api.DELETE_LINK_API + "/{ident}"
                    + " and ident should not be empty").andStatus(HttpCode.STATUS_400);
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            response.setStatus(HttpCode.STATUS_400);
            return YalseeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string")
                    .andStatus(HttpCode.STATUS_400);
        }

        //token check
        String deleteToken = request.getHeader(Header.X_YALSEE_TOKEN);
        if(StringUtils.isNotBlank(deleteToken)) {
            if (!deleteToken.equals(appUtils.getDeleteToken())) {
                response.setStatus(HttpCode.STATUS_401);
                return YalseeErrorJson.createWithMessage("Unauthorized: Wrong Delete Token")
                        .andStatus(HttpCode.STATUS_401);
            }
        } else {
            response.setStatus(HttpCode.STATUS_401);
            return YalseeErrorJson.createWithMessage("Unauthorized: Delete Token must be present")
                    .andStatus(HttpCode.STATUS_401);
        }

        //action
        DeleteResult result = linkService.deleteLink(ident);
        if(result instanceof DeleteResult.Success) {
            response.setStatus(HttpCode.STATUS_200);
            return EmptyJson.create();
        } else if(result instanceof DeleteResult.NotFound) {
            response.setStatus(HttpCode.STATUS_404);
            return YalseeErrorJson.createWithMessage(((DeleteResult.NotFound) result).getErrorMessage())
                    .andStatus(HttpCode.STATUS_404);
        } else if(result instanceof DeleteResult.DatabaseDown) {
            response.setStatus(HttpCode.STATUS_503);
            return YalseeErrorJson.createWithMessage(((DeleteResult.DatabaseDown) result).getErrorMessage())
                    .andStatus(HttpCode.STATUS_503);
        } else if (result instanceof DeleteResult.Fail) {
            log.error(((DeleteResult.Fail) result).getException().getMessage());
            response.setStatus(HttpCode.STATUS_500);
            return YalseeErrorJson.createWithMessage("Unexpected failure").andStatus(HttpCode.STATUS_500);
        } else {
            response.setStatus(HttpCode.STATUS_500);
            return YalseeErrorJson.createWithMessage("Unexpected failure").andStatus(HttpCode.STATUS_500);
        }
    }
}
