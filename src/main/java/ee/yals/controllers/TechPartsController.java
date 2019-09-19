package ee.yals.controllers;

import ee.yals.Endpoint;
import ee.yals.controllers.internal.YalsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles tech resources such as robots.txt, humans.txt and favicon.ico
 *
 * @since 2.0
 */
@Controller
public class TechPartsController extends YalsController {

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ROBOTS_TXT, produces = "text/plain")
    public String robots() {
        return "/s/robots.txt";
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.HUMANS_TXT, produces = "text/plain")
    public String humans() {
        return "/s/humans.txt";
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE, produces = "text/html")
    public void error(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        render500();
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.NOT_FOUND, produces = "text/html")
    public void notFound(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        render404();
    }

}
