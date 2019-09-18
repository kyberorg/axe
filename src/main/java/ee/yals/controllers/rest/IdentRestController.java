package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.core.IdentGenerator;
import ee.yals.json.ErrorJson;
import ee.yals.json.LinkResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.result.GetResult;
import ee.yals.services.LinkService;
import ee.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Request stored link by ident
 *
 * @since 1.0
 */
@RestController
@Slf4j
public class IdentRestController {
    private static final String TAG = "[API Get]";

    private final LinkService linkService;
    private final AppUtils appUtils;

    public IdentRestController(@Qualifier("dbStorage") LinkService linkService, AppUtils appUtils) {
        this.linkService = linkService;
        this.appUtils = appUtils;
    }

    @RequestMapping(method = RequestMethod.GET, value = {
            Endpoint.LINK_API,
            Endpoint.LINK_API_BASE
    })
    public Json getLink(HttpServletResponse response) {
        return getLink("", response);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = {
                    Endpoint.LINK_API_MAPPING,

            })
    public Json getLink(@PathVariable("ident") String ident, HttpServletResponse response) {
        log.info(String.format("%s got request: {\"Ident\": %s}", TAG, ident));

        if (StringUtils.isBlank(ident)) {
            log.info(String.format("%s Got empty ident", TAG));
            response.setStatus(400);
            return ErrorJson.createWithMessage("Request should be like this: " + Endpoint.LINK_API_MAPPING + " And ident should not be empty");
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            log.error(String.format("%s Request has not valid ident: %s", TAG, ident));
            response.setStatus(400);
            return ErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string");
        }

        GetResult result = linkService.getLink(ident);
        if (result instanceof GetResult.NotFound) {
            log.info(String.format("%s Miss {\"Ident\": %s}", TAG, ident));
            response.setStatus(404);
            return ErrorJson.createWithMessage(((GetResult.NotFound) result).getErrorMessage());
        } else if (result instanceof GetResult.Success) {
            response.setStatus(200);
            String link = ((GetResult.Success) result).getLink();
            log.info(String.format("%s Hit. {\"Ident\": %s, \"Link found\": %s}", TAG, ident, link));
            if (!appUtils.isAscii(link)) {
                // Handle international domains by detecting non-ascii and converting them to punycode
                String punycodedUrl = appUtils.covertUnicodeToAscii(link);
                log.info("{} encoding URL using punycode. Link: {} transformed to {}", TAG, link, punycodedUrl);
                link = punycodedUrl;
            }
            return LinkResponseJson.create().withLink(link);
        } else {
            log.error(String.format("%s unknown failure", TAG));
            log.debug(String.format("%s got unknown result object: %s", TAG, result));
            response.setStatus(500);
            return ErrorJson.createWithMessage("Unexpected Server error");
        }

    }
}
