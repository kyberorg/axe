package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.YalsException;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.YalsErrorJson;
import eu.yals.utils.AppUtils;
import eu.yals.utils.YalsErrorKeeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class YalsErrorController implements ErrorController {
    public static final String NO_EXCEPTION = "No exception, just something else";
    private final String TAG = "[Error Controller]";

    HttpServletRequest request;
    HttpServletResponse response;
    YalsErrorKeeper errorKeeper;

    Object rawException;
    Throwable cause;
    int status;
    String error;
    String path;

    public YalsErrorController(YalsErrorKeeper yalsErrorKeeper) {
        this.errorKeeper = yalsErrorKeeper;
    }

    @RequestMapping(Endpoint.TNT.ERROR_PAGE)
    public void handleError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.request = req;
        this.response = resp;

        status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        rawException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        path = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        cause = getCause();
        findErrorAndStatus();

        if (status < 400) {
            //there is no error
            logRequest(false);
            return;
        } else {
            logRequest(true);
        }

        YalsErrorJson errorJson = createErrorJson();
        String errorId = errorKeeper.send(errorJson);

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
                redirectToVaadinErrorPage(errorId);
            } else if (AppUtils.clientWantsJson(req)) {
                responseWithJson(errorJson);
            } else {
                resp.setHeader(Header.ACCEPT, MimeType.APPLICATION_JSON + "," + MimeType.TEXT_HTML);
                resp.setStatus(406);
            }
        } else {
            //html
            redirectToVaadinErrorPage(errorId);
        }
    }

    private Throwable getCause() {
        if (rawException == null) {
            return new NoSuchFieldException(NO_EXCEPTION);
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
        if (!(cause instanceof NoSuchFieldException)) {
            errorJson.setThrowable(cause);
        }
        return errorJson;
    }

    @Override
    public String getErrorPath() {
        return Endpoint.TNT.ERROR_PAGE;
    }

    private void logRequest(boolean realError) {
        boolean isException = !(rawException instanceof NoSuchFieldException);
        if (realError) {
            if (isException) {
                log.info("{} Status: {}. Path: {}. Exception: {}", TAG, status, path, rawException);
            } else {
                log.info("{} Status: {}. Path: {}", TAG, status, path);
            }
        } else {
            log.trace("{} Status: {}. Path: {}", TAG, status, path);
        }
    }

    private void responseWithJson(YalsErrorJson json) throws IOException {
        response.setStatus(status);
        response.setContentType(MimeType.APPLICATION_JSON);
        response.getWriter().write(json.toString());
    }

    private void redirectToVaadinErrorPage(String errorId) {
        response.setStatus(301);
        response.setHeader(Header.LOCATION, getHost() + "/" + Endpoint.UI.ERROR_PAGE_500 + "?" + App.Params.ERROR_ID + "=" + errorId);
    }

    private String getHost() {
        return request.getRequestURI().replace(getErrorPath(), "");
    }
}
