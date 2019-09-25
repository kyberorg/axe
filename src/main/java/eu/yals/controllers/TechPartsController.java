package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.internal.YalsController;
import eu.yals.json.ErrorJson;
import eu.yals.json.internal.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.PathVariable;
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
@Slf4j
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

    /**
     * This endpoint meant to be used only in application tests for simulating application fails
     *
     * @return always throws RuntimeException
     */
    @RequestMapping(method = RequestMethod.GET, value = {Endpoint.FAIL_ENDPOINT, Endpoint.FAIL_API_ENDPOINT})
    public String iWillAlwaysFail() {
        throw new RuntimeException("I will always fail");
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE)
    public String error(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        int status = 500;

        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof Exception) {
            Exception cause = (Exception) ((Exception) exception).getCause();
            if (cause instanceof CannotCreateTransactionException) {
                status = 503;
            }
        }

        if (isApiRequest(request) || clientWantsJson(request)) {
            return Endpoint.ERROR_PAGE_FOR_API_BASE + status;
        }

        if (status == 503) {
            return render503();
        } else {
            return render500();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE_FOR_API, produces = MimeType.APPLICATION_JSON)
    @ResponseBody
    public Json errorForApi(@PathVariable("status") int status, HttpServletResponse response) {
        response.setStatus(status);
        if (status == 503) {
            return ErrorJson.createWithMessage("Server is unavailable");
        } else {
            return ErrorJson.createWithMessage("Server Error");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.NOT_FOUND_PAGE)
    public String notFound(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        if (isApiRequest(request) || clientWantsJson(request)) {
            return Endpoint.NOT_FOUND_PAGE_FOR_API;
        }

        return render404();
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.NOT_FOUND_PAGE_FOR_API, produces = MimeType.APPLICATION_JSON)
    @ResponseBody
    public Json notFoundJson(HttpServletResponse response) {

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
            log.error("Failed to determine request URL which caused an error", e);
            return false;
        }
        return requestUrl.startsWith("/api");
    }
}
