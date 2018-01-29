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

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.MM_API)
    public Json mm(@RequestBody String body, HttpServletResponse response) {
        System.out.println("MM: " + body);
        response.setStatus(200);
        return ErrorJson.createWithMessage("Not impl");
    }
}
