package eu.yals;

import eu.yals.documentation.HttpMethod;
import eu.yals.documentation.RequestMethod;

/**
 * List of application endpoints
 *
 * @since 2.0
 */
public class Endpoint {
    private Endpoint() {
        throw new UnsupportedOperationException("Utility class");
    }

    @RequestMethod(value = HttpMethod.POST, api = true)
    public static final String STORE_API = "/api/store";

    @RequestMethod(value = HttpMethod.GET, api = true)
    public static final String LINK_API = "/api/link/";
    public static final String LINK_API_MAPPING = "/api/link/{ident}";
    public static final String LINK_API_BASE = "/api/link";

    @RequestMethod(value = HttpMethod.POST, api = true)
    public static final String MM_API = "/api/mm";

    @RequestMethod(value = HttpMethod.GET, api = true)
    public static final String TELEGRAM_STATUS_API = "/api/tg/status";

    @RequestMethod(HttpMethod.GET)
    public static final String SLASH_BASE = "/";
    public static final String SLASH = "/{ident}";

    @RequestMethod(HttpMethod.GET)
    public static final String ROBOTS_TXT = "/robots.txt";
    public static final String HUMANS_TXT = "/humans.txt";
    public static final String FAVICON_ICO = "/favicon.ico";
    public static final String FAIL_ENDPOINT = "/failPoint";
    public static final String FAIL_API_ENDPOINT = "/api/failPoint";

    @RequestMethod(HttpMethod.GET)
    public static final String TEST_CSS = "/s/css/test.css";

    @RequestMethod(HttpMethod.GET)
    public static final String ERROR_PAGE = "/error";
    public static final String ERROR_PAGE_FOR_API_BASE = "/error/api/";
    public static final String ERROR_PAGE_FOR_API = ERROR_PAGE_FOR_API_BASE + "{status}";
    public static final String NOT_FOUND_PAGE = "/errors/404";
    public static final String API_NOT_FOUND_PAGE = "/errors/404api";
}
