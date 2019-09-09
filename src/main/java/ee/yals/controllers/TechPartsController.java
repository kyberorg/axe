package ee.yals.controllers;

import ee.yals.Endpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles tech resources such as robots.txt, humans.txt and favicon.ico
 *
 * @since 2.0
 */
@Controller
public class TechPartsController {

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ROBOTS_TXT, produces = "text/plain")
    public String robots() {
        return "/s/robots.txt";
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.HUMANS_TXT, produces = "text/plain")
    public String humans() {
        return "/s/humans.txt";
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE, produces = "text/html")
    public String error() {
        return "errors/500";
    }

}
