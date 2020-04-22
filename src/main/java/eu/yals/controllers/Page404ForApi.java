package eu.yals.controllers;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.EndpointNotFoundJson;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

import static eu.yals.constants.HttpCode.STATUS_404;
import static eu.yals.constants.HttpCode.STATUS_406;

@Slf4j
@Controller
public class Page404ForApi {
    private final String TAG = "[" + Page404ForApi.class.getSimpleName() + "]";

    /**
     * Endpoint, which serves Page 404 for APi calls
     *
     * @param req  HTTP request
     * @param resp HTTP response
     * @throws IOException when unable to write response
     */
    @RequestMapping(Endpoint.Api.PAGE_404)
    public void handle(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        boolean hasParams = req.getQueryString() != null;
        boolean hasYalsMethodParam = false;
        boolean hasYalsPathParam = false;
        if (hasParams) {
            hasYalsMethodParam = req.getQueryString().contains("method");
            hasYalsPathParam = req.getQueryString().contains("path");
        }

        EndpointNotFoundJson payload;

        if (hasParams && hasYalsMethodParam && hasYalsPathParam) {
            HttpMethod method = HttpMethod.valueOf(req.getParameter("method"));
            String path = URLDecoder.decode(req.getParameter("path"), "UTF-8");
            logRequest(method, path);
            payload = EndpointNotFoundJson.createWithEndpoint(method, path);
        } else {
            logRequest(req);
            payload = EndpointNotFoundJson.create();
        }

        boolean hasAcceptHeader = AppUtils.hasAcceptHeader(req);
        if (hasAcceptHeader && !AppUtils.clientWantsJson(req)) {
            resp.setHeader(Header.ACCEPT, MimeType.APPLICATION_JSON);
            resp.setStatus(STATUS_406);
            return;
        }

        resp.setStatus(STATUS_404);
        resp.setContentType(MimeType.APPLICATION_JSON);
        resp.getWriter().write(payload.toString());
    }

    private void logRequest(final HttpServletRequest req) {
        log.info(String.format("%s Request: %s", TAG, req));
    }

    private void logRequest(final HttpMethod method, final String path) {
        log.info(String.format("%s %s %s", TAG, method.name(), path));
    }
}
