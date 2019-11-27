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

    /**
     * Endpoints accessible via browser
     */
    public static class UI {
        /**
         * Application home page
         */
        public static final String HOME_PAGE = "";
        /**
         * Workaround for "/{ident}" endpoint. Not intended to be accessed directly
         */
        public static final String SLASH_IDENT = "app";
    }

    /**
     * Endpoints or values used in Application tests only
     */
    public static class ForTests {
        public static final String SLASH_BASE = "/";
        public static final String LINK_API = Api.LINK_API + "/";
        public static final String QR_CODE_API = Api.QR_CODE_API + "/";
    }

    /**
     * API Endpoints
     */
    public static class Api {
        /**
         * API for storing links.
         * <p>
         * POST /api/store
         */
        public static final String STORE_API = "/api/store";

        /**
         * API for Getting links
         * <p>
         * GET /api/link/{ident}
         */
        public static final String LINK_API = "/api/link";

        /**
         * Mattermost API
         * <p>
         * POST /api/mm
         */
        public static final String MM_API = "/api/mm";

        /**
         * Show availability of Telegram API
         * <p>
         * GET /api/tg/status
         */
        public static final String TELEGRAM_STATUS_API = "/api/tg/status";

        /**
         * QR Code API
         * <p>
         * GET /api/qrCode/{ident}/{size}
         */
        public static final String QR_CODE_API = "/api/qrCode";
    }

    @RequestMethod(HttpMethod.GET)
    @Deprecated
    public static final String SLASH_VAADIN = "app";
    public static final String SLASH = "/app/{ident}";
    public static final String REDIRECTOR = "redirector";

    public static final String SAMPLE = "sample";

    @RequestMethod(HttpMethod.GET)
    public static final String ROBOTS_TXT = "/robots.txt";
    public static final String HUMANS_TXT = "/humans.txt";
    public static final String FAVICON_ICO = "/favicon.ico";

    // those two endpoints are used only in tests to simulate application error
    public static final String FAIL_ENDPOINT = "/failPoint";
    public static final String FAIL_API_ENDPOINT = "/api/failPoint";

    @RequestMethod(HttpMethod.GET)
    public static final String TEST_CSS = "/s/css/test.css";

    @RequestMethod(HttpMethod.GET)
    public static final String ERROR_PAGE = "/error";
    public static final String VAADIN_ERROR_PAGE = "errors/500";
    public static final String VAADIN_APPLICATION_ERROR_PAGE = "errors/503";
    public static final String NOT_FOUND_PAGE = "errors/404";
    public static final String NOT_FOUND_PAGE_FOR_API = "/errors/404api";
}
