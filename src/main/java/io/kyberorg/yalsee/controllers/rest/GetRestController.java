package io.kyberorg.yalsee.controllers.rest;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.json.LinkResponseJson;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.result.GetResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Request stored link by ident.
 *
 * @since 1.0
 */
@Slf4j
@RestController
public class GetRestController {
    private static final String TAG = "[" + GetRestController.class.getSimpleName() + "]";

    private final LinkService linkService;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linksService service for retrieving links
     */
    public GetRestController(final LinkService linksService) {
        this.linkService = linksService;
    }

    /**
     * Handles get links requests. When no ident given.
     *
     * @param response HTTP response
     * @return json which given in response
     */
    @RequestMapping(method = RequestMethod.GET, value = {
            Endpoint.Api.LINK_API,
            Endpoint.Api.LINK_API + "/"
    })
    public YalseeJson getLink(final HttpServletResponse response) {
        return getLink("", response);
    }

    /**
     * Handles get links requests. Ident version.
     *
     * @param ident    short part of URL
     * @param response HTTP response
     * @return json which given in response
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.LINK_API + "/{ident}")
    public YalseeJson getLink(final @PathVariable("ident") String ident, final HttpServletResponse response) {
        log.info("{} got request: {\"Ident\": {}}", TAG, ident);

        if (StringUtils.isBlank(ident)) {
            log.info("{} Got empty ident", TAG);
            response.setStatus(HttpCode.STATUS_400);
            return YalseeErrorJson.createWithMessage("Request should be like this: " + Endpoint.Api.LINK_API + "/{ident}"
                    + " and ident should not be empty").andStatus(HttpCode.STATUS_400);
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            response.setStatus(HttpCode.STATUS_400);
            return YalseeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string").andStatus(HttpCode.STATUS_400);
        }

        GetResult result = linkService.getLink(ident);
        if (result instanceof GetResult.NotFound) {
            log.info("{} Miss {\"Ident\": {}}", TAG, ident);
            response.setStatus(HttpCode.STATUS_404);
            return YalseeErrorJson.createWithMessage(((GetResult.NotFound) result).getErrorMessage())
                    .andStatus(HttpCode.STATUS_404);
        } else if (result instanceof GetResult.Success) {
            response.setStatus(HttpCode.STATUS_200);
            String link = ((GetResult.Success) result).getLink();
            log.info("{} Hit. {\"Ident\": {}, \"Link found\": {}}", TAG, ident, link);
            if (!AppUtils.isAscii(link)) {
                // Handle international domains by detecting non-ascii and converting them to punycode
                String punycodedUrl = AppUtils.covertUnicodeToAscii(link);
                log.info("{} encoding URL using punycode. Link: {} transformed to {}", TAG, link, punycodedUrl);
                link = punycodedUrl;
            }
            return LinkResponseJson.create().withLink(link);
        } else if (result instanceof GetResult.DatabaseDown) {
            response.setStatus(HttpCode.STATUS_503);
            log.error("{} Database is DOWN", TAG, ((GetResult.DatabaseDown) result).getException());
            return YalseeErrorJson.createWithMessage("The server is currently unable to handle the request")
                    .andStatus(HttpCode.STATUS_503);
        } else {
            log.error("{} unknown failure", TAG);
            log.debug("{} got unknown result object: {}", TAG, result);
            response.setStatus(HttpCode.STATUS_500);
            return YalseeErrorJson.createWithMessage("Unexpected Server error");
        }
    }
}
