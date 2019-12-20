package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.exception.error.YalsError;
import eu.yals.json.YalsErrorJson;
import eu.yals.utils.AppUtils;
import eu.yals.utils.ErrorUtils;
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
    private final String TAG = "[Error Controller]";

    private final YalsErrorKeeper errorKeeper;
    private final ErrorUtils errorUtils;

    HttpServletRequest request;
    HttpServletResponse response;

    Object rawException;
    Throwable cause;
    int status;
    String path;

    public YalsErrorController(YalsErrorKeeper yalsErrorKeeper, ErrorUtils errorUtils) {
        this.errorKeeper = yalsErrorKeeper;
        this.errorUtils = errorUtils;
    }

    @RequestMapping(Endpoint.TNT.ERROR_PAGE)
    public void handleError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.request = req;
        this.response = resp;

        rawException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        path = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        status = getCorrectStatus();

        if (status < 400) {
            //there is no error
            logRequest(false);
            return;
        } else {
            logRequest(true);
        }

        ErrorUtils.Args args = ErrorUtils.ArgsBuilder.withException((Throwable) rawException)
                .addStatus(status)
                .addPath(path)
                .build();
        YalsError yalsError = errorUtils.convertExceptionToYalsError(args);

        YalsErrorJson errorJson = YalsErrorJson.createFromYalsError(yalsError);
        String errorId = storeYalsError(yalsError);

        errorUtils.reportToBugsnag(yalsError);

        boolean hasAcceptHeader = AppUtils.hasAcceptHeader(req);
        boolean isApiCall = AppUtils.isApiRequest(req);

        log.debug("{} Has Accept Header: {}", TAG, hasAcceptHeader);
        log.debug("{} Is API Call: {}", TAG, isApiCall);

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

    @Override
    public String getErrorPath() {
        return Endpoint.TNT.ERROR_PAGE;
    }

    private int getCorrectStatus() {
        if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) == null) {
            status = 500;
        } else {
            try {
                status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            } catch (Exception e) {
                status = 500;
            }

            //DB is DOWN
            if (cause instanceof CannotCreateTransactionException) {
                status = 503;
            }
        }
        return status;
    }

    private void logRequest(boolean realError) {
        boolean isException = (rawException != null);
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

    private String storeYalsError(YalsError yalsError) {
        return errorKeeper.send(yalsError);
    }

    private void responseWithJson(YalsErrorJson json) throws IOException {
        response.setStatus(status);
        response.setContentType(MimeType.APPLICATION_JSON);
        response.getWriter().write(json.toString());
    }

    private void redirectToVaadinErrorPage(String errorId) {
        String host = request.getRequestURI().replace(getErrorPath(), "");
        response.setStatus(301);
        response.setHeader(Header.LOCATION, host + "/" + Endpoint.UI.ERROR_PAGE_500 + "?" + App.Params.ERROR_ID + "=" + errorId);
    }

}
