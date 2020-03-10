package eu.yals;

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
         * Page that will appear, when user hits location which is not exist nor served by application
         */
        public static final String PAGE_404 = "errors/404";

        /**
         * Page that will shown when application hits server-side error
         */
        public static final String ERROR_PAGE_500 = "errors/500";

        /**
         * Page that will shown when application hits fatal error and became unaccessible
         */
        public static final String ERROR_PAGE_503 = "errors/503";

        /**
         * Page for debugging staff (avoid adding it in PROD mode)
         */
        public static final String DEBUG_PAGE = "debug";

        /**
         * Page for displaying software info (avoid adding it in PROD mode)
         */
        public static final String INFO_PAGE = "infoPage";
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

        /**
         * Page 404 for API requests
         */
        public static final String PAGE_404 = "/errors/404api";
    }

    public static class Static {
        /**
         * Old good file for search engines
         */
        public static final String ROBOTS_TXT = "/robots.txt";

        /**
         * Because there are not only robots behind the scenes
         */
        public static final String HUMANS_TXT = "/humans.txt";

        /**
         * Application icon
         */
        public static final String FAVICON_ICO = "/favicon.ico";
    }

    /**
     * Tech and temp
     */
    public static class TNT {
        /**
         * Workaround for "/{ident}" endpoint. Not intended to be accessed directly
         */
        public static final String SLASH_IDENT = "app";

        /**
         * Endpoint for redirecting to long links. Not intended to be accessed directly
         */
        public static final String REDIRECTOR = "redirector";

        /**
         * Catch-all errors endpoint. Served by:
         * <p>
         * {@link eu.yals.controllers.YalsErrorController}
         */
        public static final String ERROR_PAGE = "/error";

        /**
         * Temporary location with old UI
         */
        public static final String SAMPLE = "sample";
    }

    /**
     * Endpoints or values used in Application tests only
     */
    public static class ForTests {
        public static final String SLASH_BASE = "/";
        public static final String LINK_API = Api.LINK_API + "/";
        public static final String QR_CODE_API = Api.QR_CODE_API + "/";

        // those two endpoints are used only in tests to simulate application error
        public static final String FAIL_ENDPOINT = "/failPoint";
        public static final String FAIL_API_ENDPOINT = "/api/failPoint";
    }


}
