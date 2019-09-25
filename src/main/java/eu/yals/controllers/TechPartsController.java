package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.internal.YalsController;
import eu.yals.json.ErrorJson;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.RequestDispatcher;
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
    public String error(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof Exception) {
            Exception cause = (Exception) ((Exception) exception).getCause();
            if (cause instanceof CannotCreateTransactionException) {
                return render503();
            }
        }
        return render500();
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.NOT_FOUND_PAGE)
    public String notFound(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        return render404();
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.NOT_FOUND_PAGE, produces = MimeType.APPLICATION_JSON)
    @ResponseBody
    public String notFoundJson(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        response.setStatus(404);
        return ErrorJson.createWithMessage("Not Found").toString();
    }

    private boolean clientAcceptsJson(HttpServletRequest request) {
        String acceptHeader = request.getHeader(Header.ACCEPT);
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }

    }
}
