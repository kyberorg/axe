package ee.yals.controllers.internal;

import ee.yals.utils.AppUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Common methods for all controller
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@Component
public class YalsController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected String render404() {
        response.setStatus(404);
        return "errors/404";
    }

    protected String render404Ident() {
        response.setStatus(404);
        return "errors/404-ident";
    }

    protected String render500() {
        response.setStatus(500);
        return "errors/500";
    }

    protected String redirect(String url) {
        response.setStatus(302);
        response.setHeader(AppUtils.LOCATION_HEADER_NAME, url);
        return "";
    }

    protected boolean hasTestHeader() {
        return Objects.nonNull(request.getHeader(AppUtils.TEST_HEADER_NAME));
    }
}
