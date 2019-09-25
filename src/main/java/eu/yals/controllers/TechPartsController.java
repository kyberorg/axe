package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.internal.YalsController;
import eu.yals.json.ErrorJson;
import eu.yals.json.internal.Json;
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

        if (isApiRequest(request) || clientWantsJson(request)) {
            return Endpoint.API_NOT_FOUND_PAGE;
        }

        return render404();
    }

    @RequestMapping(method = RequestMethod.GET, value = {Endpoint.API_NOT_FOUND_PAGE, Endpoint.NOT_FOUND_PAGE},
            produces = MimeType.APPLICATION_JSON)
    @ResponseBody
    public Json notFoundJson(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        response.setStatus(404);
        return ErrorJson.createWithMessage("Page Not Found");
    }

    private boolean clientWantsJson(HttpServletRequest request) {
        String acceptHeader = request.getHeader(Header.ACCEPT);
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestUrl;
        try {
            requestUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        } catch (Exception e) {
            return false;
        }
        return requestUrl.startsWith("/api");
    }
}
