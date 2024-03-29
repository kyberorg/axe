package pm.axe.controllers;

import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.exception.error.AxeError;
import pm.axe.json.AxeErrorJson;
import pm.axe.utils.AppUtils;
import pm.axe.utils.AxeErrorKeeper;
import pm.axe.utils.ErrorUtils;
import pm.axe.utils.RedirectLoopDetector;

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
@RequiredArgsConstructor
@Controller
public class AxeErrorController implements ErrorController {
    private final String TAG = "[" + AxeErrorController.class.getSimpleName() + "]";

    private final AxeErrorKeeper errorKeeper;
    private final RedirectLoopDetector loopDetector;
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

        if (status < HttpStatus.BAD_REQUEST) {
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
        AxeError axeError = errorUtils.convertExceptionToAxeError(args);

        AxeErrorJson errorJson = AxeErrorJson.createFromAxeError(axeError);
        String errorId = storeAxeError(axeError);

        boolean isWebFirewallInAction = axeError.getRawException() != null
                && axeError.getRawException() instanceof RequestRejectedException;

        if (isWebFirewallInAction) {
            axeError.setMessageToUser("Web Firewall rejected this request "
                    + "because the URL contained a potentially malicious String");
            axeError.setHttpStatus(HttpStatus.FORBIDDEN);
        }

        errorUtils.reportToBugsnag(axeError);

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
                response.setHeader(Axe.Headers.ACCEPT, MimeTypes.JSON);
                response.setStatus(HttpStatus.NOT_ACCEPTABLE);
            }
            return;
        }

        if (hasAcceptHeader) {
            if (AppUtils.clientWantsHtml(request)) {
                redirectToVaadinErrorPage(errorId);
            } else if (AppUtils.clientWantsJson(req)) {
                responseWithJson(errorJson);
            } else {
                resp.setHeader(Axe.Headers.ACCEPT, MimeTypes.JSON + "," + MimeTypes.HTML);
                resp.setStatus(HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            //html
            redirectToVaadinErrorPage(errorId);
        }
    }

    private int getCorrectStatus() {
        if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            } catch (Exception e) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            //DB is DOWN
            if (cause instanceof CannotCreateTransactionException) {
                status = HttpStatus.SERVICE_UNAVAILABLE;
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

    private String storeAxeError(final AxeError axeError) {
        return errorKeeper.send(axeError);
    }

    private void responseWithJson(final AxeErrorJson json) throws IOException {
        response.setStatus(status);
        response.setContentType(MimeTypes.JSON);
        response.getWriter().write(json.toString());
    }

    private void redirectToVaadinErrorPage(final String errorId) {
        if (rawException instanceof Error || cause instanceof Error) {
            redirectToAppDownAnalogPage();
            return;
        }
        loopDetector.updateCounter();
        final String errorPageRoute = loopDetector.isLoopDetected()
                ? Endpoint.UI.RAW_ERROR_PAGE_500 : Endpoint.UI.ERROR_PAGE_500;

        response.setStatus(HttpStatus.TEMPORARY_REDIRECT);
        response.setHeader(Axe.Headers.LOCATION, "/" + errorPageRoute + "?"
                + Axe.Params.ERROR_ID + "=" + errorId);
    }

    private void redirectToAppDownAnalogPage() {
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT);
        response.setHeader(Axe.Headers.LOCATION, Endpoint.TNT.APP_OFFLINE);
    }

}
