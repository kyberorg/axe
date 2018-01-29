package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.ErrorJson;
import ee.yals.json.internal.Json;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * MatterMost chat endpoint
 *
 * @since 2.3
 */
@RestController
public class MMRestController {

    private static final String PAYLOAD_MARKER_TEXT = "text=";

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletResponse response) {
        String[] bodyParams = body.split("&");
        if (bodyParams.length <= 1) {
            response.setStatus(421);
            return ErrorJson.createWithMessage("Body should be a valid request from MatterMost");
        }

        for (String bodyParam : bodyParams) {
            if (bodyParam.startsWith(PAYLOAD_MARKER_TEXT)) {
                String link = bodyParam.replaceFirst("text=", "");
                System.out.println("MM Link: " + link);
            }
        }

        response.setStatus(200);
        return ErrorJson.createWithMessage("Not impl");
    }
}
