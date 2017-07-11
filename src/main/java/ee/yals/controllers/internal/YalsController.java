package ee.yals.controllers.internal;

import ee.yals.constants.Header;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Common methods for all controller
 *
 * @since 2.0
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
        response.setHeader(Header.LOCATION, url);
        return "";
    }

    protected boolean hasTestHeader() {
        return Objects.nonNull(request.getHeader(Header.TEST));
    }
}
