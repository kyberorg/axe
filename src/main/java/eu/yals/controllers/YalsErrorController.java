package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.YalsErrorJson;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Controller
public class YalsErrorController implements ErrorController {
    private final String TAG = "[Error Controller]";

    HttpServletRequest request;
    HttpServletResponse response;

    Object rawException;
    Throwable cause;
    int status;
    String error;
    String path;

    @RequestMapping(Endpoint.TNT.ERROR_PAGE)
    public void handleError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.request = req;
        this.response = resp;

        status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        rawException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        path = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        cause = getCause();
        findErrorAndStatus();

        YalsErrorJson errorJson = createErrorJson();

        boolean hasAcceptHeader = AppUtils.hasAcceptHeader(req);
        boolean isApiCall = AppUtils.isApiRequest(req);

        if (isApiCall) {
            boolean clientHasNoAcceptHeader = !hasAcceptHeader;
            if (clientHasNoAcceptHeader || AppUtils.clientWantsJson(request)) {
                responseWithJson(errorJson);
                return;
            } else {
                response.setHeader(Header.ACCEPT, MimeType.APPLICATION_JSON);
                response.setStatus(406);
            }
            return;
        }

        if (hasAcceptHeader) {
            if (AppUtils.clientWantsHtml(request)) {
                redirectToVaadinErrorPage(errorJson);
            } else if (AppUtils.clientWantsJson(req)) {
                responseWithJson(errorJson);
            } else {
                resp.setHeader(Header.ACCEPT, MimeType.APPLICATION_JSON + "," + MimeType.TEXT_HTML);
                resp.setStatus(406);
            }
        } else {
            redirectToVaadinErrorPage(errorJson);
        }
    }

    private Throwable getCause() {
        if (rawException == null) {
            log.error("{} Have you set exception before calling getCause() ?", TAG);
            return new Exception("No exception, just something else");
        }
        return ((Exception) rawException).getCause();
    }

    private void findErrorAndStatus() {

        if (cause instanceof CannotCreateTransactionException) {
            log.error("{} Database is DOWN", TAG, cause);
            error = "Application is down";
            status = 503;
        } else {
            if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null) {
                status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
                error = "Error Code: " + status;
            } else {
                log.error("{} Fatal Error is {}", TAG, cause.getMessage(), cause);
                error = "Internal Server Error";
                status = 500;
            }
        }
    }

    private YalsErrorJson createErrorJson() {
        YalsErrorJson errorJson = YalsErrorJson.withMessage(cause.getMessage());
        errorJson.setStatus(status);
        errorJson.setError(error);
        errorJson.setPath(path);
        return errorJson;
    }

    @Override
    public String getErrorPath() {
        return Endpoint.TNT.ERROR_PAGE;
    }


    private void responseWithJson(YalsErrorJson json) throws IOException {
        response.setStatus(status);
        response.setContentType(MimeType.APPLICATION_JSON);
        response.getWriter().write(json.toString());
    }

    private String encodeJson(YalsErrorJson json) {
        byte[] bytes = json.toString().getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void redirectToVaadinErrorPage(YalsErrorJson json) {
        response.setStatus(301);
        response.setHeader(Header.LOCATION, Endpoint.UI.ERROR_PAGE_500 + "/" + encodeJson(json));
    }
}
