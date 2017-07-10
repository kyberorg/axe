package ee.yals;

import ee.yals.utils.HttpMethod;
import ee.yals.utils.RequestMethod;

/**
 * List of application endpoints
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public class Endpoint {
    private Endpoint() {
        throw new UnsupportedOperationException("Utility class");
    }

    @RequestMethod(value = HttpMethod.POST, api = true)
    public static final String STORE_API = "/api/store";

    @RequestMethod(value = HttpMethod.GET, api = true)
    public static final String LINK_API= "/api/link/";
    public static final String LINK_API_MAPPING= "/api/link/{ident}";
    public static final String LINK_API_BASE= "/api/link";

    @RequestMethod(HttpMethod.GET)
    public static final String SLASH_BASE = "/";
    public static final String SLASH= "/{ident}";

    @RequestMethod(HttpMethod.GET)
    public static final String ROBOTS_TXT = "/robots.txt";
    public static final String HUMANS_TXT = "/humans.txt";
    public static final String FAVICON_ICO = "/favicon.ico";

    @RequestMethod(HttpMethod.GET)
    public static final String TEST_CSS = "/s/css/test.css";
}
