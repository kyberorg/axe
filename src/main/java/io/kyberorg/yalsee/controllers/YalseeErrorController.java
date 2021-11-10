package io.kyberorg.yalsee.controllers;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.exception.error.YalseeError;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import io.kyberorg.yalsee.utils.YalseeErrorKeeper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles application errors.
 *
 * @since 2.7
 */
@Slf4j
@AllArgsConstructor
@Controller
public class YalseeErrorController implements ErrorController {
    private final String TAG = "[" + YalseeErrorController.class.getSimpleName() + "]";

    private final YalseeErrorKeeper errorKeeper;
    private final ErrorUtils errorUtils;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private Throwable rawException;
    private Throwable cause;
    private int status;
    private String path;

    /**
     * Error handling endpoint.
     *
     * @param req  HTTP request
     * @param resp HTTP response
     * @throws IOException when failed to write response
     */
    @RequestMapping(Endpoint.TNT.ERROR_PAGE)
    public void handleError(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        this.request = req;
        this.response = resp;

        rawException = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (rawException != null) {
            cause = rawException.getCause();
        }
        path = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        status = getCorrectStatus();

        if (status < HttpCode.STATUS_400) {
            //there is no error
            logRequest(false);
            return;
        } else {
            logRequest(true);
        }

        ErrorUtils.Args args = ErrorUtils.ArgsBuilder.withException(rawException)
                .addStatus(status)
                .addPath(path)
                .build();
        YalseeError yalseeError = errorUtils.convertExceptionToYalseeError(args);

        YalseeErrorJson errorJson = YalseeErrorJson.createFromYalseeError(yalseeError);
        String errorId = storeYalseeError(yalseeError);

        errorUtils.reportToBugsnag(yalseeError);

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
                response.setStatus(HttpCode.STATUS_406);
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
                resp.setStatus(HttpCode.STATUS_406);
            }
        } else {
            //html
            redirectToVaadinErrorPage(errorId);
        }
    }

    private int getCorrectStatus() {
        if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) == null) {
            status = HttpCode.STATUS_500;
        } else {
            try {
                status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            } catch (Exception e) {
                status = HttpCode.STATUS_500;
            }

            //DB is DOWN
            if (cause instanceof CannotCreateTransactionException) {
                status = HttpCode.STATUS_503;
            }
        }
        return status;
    }

    private void logRequest(final boolean realError) {
        boolean isException = (rawException != null);
        if (realError) {
            if (isException) {
                log.info("{} Status: {}. Path: {}. Exception message: {}",
                        TAG, status, path, rawException.getMessage());
                log.debug("{} Exception: {}", TAG, rawException);
            } else {
                log.info("{} Status: {}. Path: {}", TAG, status, path);
            }
        } else {
            log.trace("{} Status: {}. Path: {}", TAG, status, path);
        }
    }

    private String storeYalseeError(final YalseeError yalseeError) {
        return errorKeeper.send(yalseeError);
    }

    private void responseWithJson(final YalseeErrorJson json) throws IOException {
        response.setStatus(status);
        response.setContentType(MimeType.APPLICATION_JSON);
        response.getWriter().write(json.toString());
    }

    private void redirectToVaadinErrorPage(final String errorId) {
        if (rawException instanceof Error || cause instanceof Error) {
            redirectToAppDownAnalogPage();
            return;
        }
        response.setStatus(HttpCode.STATUS_301);
        response.setHeader(Header.LOCATION, "/" + Endpoint.UI.ERROR_PAGE_500 + "?"
                + App.Params.ERROR_ID + "=" + errorId);
    }

    private void redirectToAppDownAnalogPage() {
        response.setStatus(HttpCode.STATUS_301);
        response.setHeader(Header.LOCATION, Endpoint.TNT.APP_OFFLINE);
    }

}
