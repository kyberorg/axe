package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.controllers.internal.YalsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles tech resources such as robots.txt, humans.txt and favicon.ico
 *
 * @since 2.0
 */
@Slf4j
@Controller
public class TechPartsController extends YalsController {

    /**
     * This endpoint meant to be used only in application tests for simulating application fails
     *
     * @return always throws RuntimeException
     */
    @RequestMapping(method = RequestMethod.GET, value = {Endpoint.FAIL_ENDPOINT, Endpoint.FAIL_API_ENDPOINT})
    public String iWillAlwaysFail() {
        throw new RuntimeException("I will always fail");
    }

  /*  @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE)
    public String error(HttpServletRequest request, HttpServletResponse response) {
        final String TAG = "[Error Controller]";

        this.request = request;
        this.response = response;

        int status = 500;

        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof Exception) {
            Exception cause = (Exception) ((Exception) exception).getCause();
            if (cause instanceof CannotCreateTransactionException) {
                log.error("{} Database is DOWN", TAG, cause);
                status = 503;
            } else {
                log.error("{} Fatal Error is {}", TAG, cause.getMessage(), cause);
            }
        } else {
            Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            try {
                status = (int) statusCode;
                //log.trace("{} Unknown Error with status {}", TAG, statusCode);
            } catch (ClassCastException e) {
                status = 500;
            }
        }

        if (isApiRequest(request) || clientWantsJson(request)) {
            return Endpoint.ERROR_PAGE_FOR_API_BASE + status;
        }

        switch (status) {
            case 300-399:
                return "";
            case 400:
                return render400();
            case 503:
                return render503();
            case 500:
            default:
                return render500();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.ERROR_PAGE_FOR_API, produces = MimeType.APPLICATION_JSON)
    @ResponseBody
    public Json errorForApi(@PathVariable("status") int status, HttpServletResponse response) {
        response.setStatus(status);

        switch (status) {
            case 400:
                return ErrorJson.createWithMessage("Bad request");
            case 503:
                return ErrorJson.createWithMessage("Server is unavailable");
            case 500:
            default:
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
    }*/
}
