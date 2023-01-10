package pm.axe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.HttpMethod;
import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.json.EndpointNotFoundResponse;
import pm.axe.utils.AppUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class Page404ForApi {
    private final String TAG = "[" + Page404ForApi.class.getSimpleName() + "]";

    /**
     * Endpoint, which serves Page 404 for APi calls.
     *
     * @param req  HTTP request
     * @param resp HTTP response
     * @throws IOException when unable to write response
     */
    @RequestMapping(Endpoint.Api.PAGE_404)
    public void handle(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        boolean hasParams = req.getQueryString() != null;
        boolean hasMethodParam = false;
        boolean hasPathParam = false;
        if (hasParams) {
            hasMethodParam = req.getQueryString().contains("method");
            hasPathParam = req.getQueryString().contains("path");
        }

        EndpointNotFoundResponse payload;

        if (hasParams && hasMethodParam && hasPathParam) {
            HttpMethod method = HttpMethod.valueOf(req.getParameter("method"));
            String path = URLDecoder.decode(req.getParameter("path"), StandardCharsets.UTF_8);
            logRequest(method, path);
            payload = EndpointNotFoundResponse.createWithEndpoint(method, path);
        } else {
            logRequest(req);
            payload = EndpointNotFoundResponse.create();
        }

        boolean hasAcceptHeader = AppUtils.hasAcceptHeader(req);
        if (hasAcceptHeader && !AppUtils.clientWantsJson(req)) {
            resp.setHeader(Axe.Headers.ACCEPT, MimeTypes.JSON);
            resp.setStatus(HttpStatus.NOT_ACCEPTABLE);
            return;
        }

        resp.setStatus(HttpStatus.NOT_FOUND);
        resp.setContentType(MimeTypes.JSON);
        resp.getWriter().write(payload.toString());
    }

    private void logRequest(final HttpServletRequest req) {
        log.info(String.format("%s Request: %s", TAG, req));
    }

    private void logRequest(final HttpMethod method, final String path) {
        log.info(String.format("%s %s %s", TAG, method.name(), path));
    }
}
