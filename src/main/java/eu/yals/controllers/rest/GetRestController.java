package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import eu.yals.json.LinkResponseJson;
import eu.yals.json.YalsErrorJson;
import eu.yals.json.YalsJson;
import eu.yals.result.GetResult;
import eu.yals.services.LinkService;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static eu.yals.constants.HttpCode.*;

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
    public YalsJson getLink(final HttpServletResponse response) {
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
    public YalsJson getLink(final @PathVariable("ident") String ident, final HttpServletResponse response) {
        log.info("{} got request: {\"Ident\": {}}", TAG, ident);

        if (StringUtils.isBlank(ident)) {
            log.info("{} Got empty ident", TAG);
            response.setStatus(STATUS_400);
            return YalsErrorJson.createWithMessage("Request should be like this: " + Endpoint.Api.LINK_API + "/{ident}"
                    + " and ident should not be empty").andStatus(STATUS_400);
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            response.setStatus(STATUS_400);
            return YalsErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string").andStatus(STATUS_400);
        }

        GetResult result = linkService.getLink(ident);
        if (result instanceof GetResult.NotFound) {
            log.info("{} Miss {\"Ident\": {}}", TAG, ident);
            response.setStatus(STATUS_404);
            return YalsErrorJson.createWithMessage(((GetResult.NotFound) result).getErrorMessage())
                    .andStatus(STATUS_404);
        } else if (result instanceof GetResult.Success) {
            response.setStatus(STATUS_200);
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
            response.setStatus(STATUS_503);
            log.error("{} Database is DOWN", TAG, ((GetResult.DatabaseDown) result).getException());
            return YalsErrorJson.createWithMessage("The server is currently unable to handle the request")
                    .andStatus(STATUS_503);
        } else {
            log.error("{} unknown failure", TAG);
            log.debug("{} got unknown result object: {}", TAG, result);
            response.setStatus(STATUS_500);
            return YalsErrorJson.createWithMessage("Unexpected Server error");
        }
    }
}
