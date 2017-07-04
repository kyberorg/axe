package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.ErrorJson;
import ee.yals.json.Json;
import ee.yals.json.LinkResponseJson;
import ee.yals.result.GetResult;
import ee.yals.services.LinkService;
import ee.yals.utils.IdentGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Request stored link by ident
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@RestController
public class IdentRestController {

    @Autowired
    @Qualifier("dbStorage")
    private LinkService linkService;

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
        if (StringUtils.isBlank(ident)) {
            response.setStatus(400);
            return ErrorJson.createWithMessage("Request should be like this: " + Endpoint.LINK_API_MAPPING + " And ident should not be empty");
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isIdentValid) {
            response.setStatus(400);
            return ErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string");
        }

        GetResult result = linkService.getLink(ident);
        if (result instanceof GetResult.NotFound) {
            response.setStatus(404);
            return ErrorJson.createWithMessage(((GetResult.NotFound) result).getErrorMessage());
        } else if (result instanceof GetResult.Success) {
            response.setStatus(200);
            return LinkResponseJson.create().withLink(((GetResult.Success) result).getLink());
        } else {
            response.setStatus(500);
            return ErrorJson.createWithMessage("Unexpected Server error");
        }

    }
}
