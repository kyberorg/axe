package pm.axe;

import pm.axe.controllers.AppOfflineController;
import pm.axe.controllers.AxeErrorController;
import pm.axe.ui.pages.err.err500.ServerErrorPage;
import pm.axe.ui.pages.err.raw500.RawServerErrorLoopPage;

/**
 * List of application endpoints.
 *
 * @since 2.0
 */
public final class Endpoint {
    private Endpoint() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Endpoints accessible via browser.
     */
    public static class UI {
        /**
         * Application home page.
         */
        public static final String HOME_PAGE = "";

        /**
         * Page that will appear, when user hits location which is not exist nor served by application.
         */
        public static final String PAGE_404 = "errors/404page";

        /**
         * Page that will appear, when user hits ident which is not exist.
         */
        public static final String IDENT_404 = "errors/404ident";

        /**
         * Page that will appear when application hits server-side error.
         */
        public static final String ERROR_PAGE_500 = "errors/500";

        /**
         * Page that will appear when application hits server-side error several times aka loop detected.
         */
        public static final String RAW_ERROR_PAGE_500 = "errors/500raw";

        /**
         * Page that will appear when application hits fatal error and became inaccessible.
         */
        public static final String ERROR_PAGE_503 = "errors/503";

        /**
         * Page for debugging staff (avoid adding it in PROD mode).
         */
        public static final String DEBUG_PAGE = "debug";

        /**
         * Page for displaying software info (avoid adding it in PROD mode).
         */
        public static final String APP_INFO_PAGE = "appInfo";

        /**
         * Page with user's links or current session links.
         */
        public static final String MY_LINKS_PAGE = "myLinks";

        /**
         * Settings Page.
         */
        public static final String SETTINGS_PAGE = "settings";

        /**
         * Meet Axe Page.
         */
        public static final String MEET_AXE_PAGE = "meetAxe";

        /**
         * Account Confirmation Page.
         */
        public static final String CONFIRMATION_PAGE = "confirmation";

        /**
         * Password Reset Page.
         */
        public static final String PASSWORD_RESET_PAGE = "password-reset";
        public static final String WELCOME_PAGE = "welcome";
        public static final String REGISTRATION_FAILED_PAGE = "registrationFailed";
        public static final String LOGIN_PAGE = "login";
    }

    /**
     * API Endpoints.
     */
    public static class Api {
        /**
         * API for manipulating with links.
         * <p>
         * /api/links
         */
        public static final String LINKS_API = "/api/links";

        /**
         * Link API + / + ident. For dokumentation.
         */
        public static final String LINKS_API_PLUS_IDENT = LINKS_API + "/{ident}";

        /**
         * API for deleting links.
         * <p>
         * DELETE /api/links/{ident}
         */
        public static final String DELETE_LINKS_API = LINKS_API + "/{ident}";

        /**
         * API for getting links.
         * <p>
         * GET /api/links/{ident}
         */
        public static final String GET_LINKS_API = LINKS_API + "/{ident}";

        /**
         * API for manipulating with QR Codes.
         * <p>
         * /api/qr
         */
        public static final String QR_API = "/api/qr";

        /**
         * API for getting QR Code with default size for short links.
         * <p>
         * GET /api/qr/{ident}
         */
        public static final String GET_QR_WITH_IDENT = QR_API + "/{ident}";

        /**
         * API for getting QR Code with user defined size.
         * <p>
         * GET /api/qr/{ident}/{size}
         */
        public static final String GET_QR_WITH_IDENT_AND_SIZE = GET_QR_WITH_IDENT + "/{size}";

        /**
         * API for getting QR Code with user defined width and height.
         * <p>
         * GET /api/qr/{ident}/{width}/{height}
         */
        public static final String GET_QR_WITH_IDENT_WIDTH_AND_HEIGHT = GET_QR_WITH_IDENT + "/{width}/{height}";

        /**
         * Mattermost API.
         * <p>
         * POST /api/mm
         */
        public static final String MM_API = "/api/mm";

        /**
         * Show availability of Telegram API.
         * <p>
         * GET /api/tg/status
         */
        public static final String TELEGRAM_STATUS_API = "/api/tg/status";

        /**
         * Page 404 for API requests.
         */
        public static final String PAGE_404 = "/errors/404api";

        /**
         * API for manipulating with Users.
         * <p>
         * /api/users
         */
        public static final String USERS_API = "/api/user";

        /**
         * Users Registration.
         */
        public static final String REGISTER_USER_API = USERS_API + "/";

        /**
         * User Deletion.
         */
        public static final String DELETE_USER_API = USERS_API + "/{username}";
        public static final String GC_USER_API = USERS_API + "/gc";
    }

    public static class Static {
        /**
         * Old good file for search engines.
         */
        public static final String ROBOTS_TXT = "/robots.txt";

        /**
         * Because there are not only robots behind the scenes.
         */
        public static final String HUMANS_TXT = "/humans.txt";

        /**
         * Application icon.
         */
        public static final String FAVICON_ICO = "/favicon.ico";

        /**
         * Sitemap, which is needed for good SEO.
         */
        public static final String SITEMAP_XML = "/sitemap.xml";

        /**
         * Application offline page.
         */
        public static final String APP_OFFLINE_PAGE = "offline-page.html";
    }

    /**
     * Tech and temp.
     */
    public static class TNT {
        /**
         * Endpoint for redirecting to long links. Not intended to be accessed directly
         */
        public static final String REDIRECT_PAGE = "redirect-page";

        /**
         * Catch-all errors endpoint. Served by:
         * <p>
         * {@link AxeErrorController}
         */
        public static final String ERROR_PAGE = "/error";

        /**
         * Serves offline page with status 503.
         * <p>
         * {@link AppOfflineController}
         */
        public static final String APP_OFFLINE = "/app-offline";

        /**
         * Server Error Loopback View. Intended to be used only within {@link ServerErrorPage}.
         */
        public static final String SERVER_ERROR_LOOP = "server-error-loop";

        /**
         * Raw Server Error Loopback View. Intended to be used only within {@link RawServerErrorLoopPage}.
         */
        public static final String RAW_SERVER_ERROR_LOOP = "raw-server-error-loop";

        /**
         * Site Preview image for SEO.
         */
        public static final String PREVIEW_IMAGE = "/preview.png";
    }

    /**
     * Endpoints or values used in Application tests only.
     */
    public static class ForTests {
        /**
         * Just slash symbol (/). Application base.
         */
        public static final String SLASH_BASE = "/";

        // those two endpoints are used only in tests to simulate application error
        /**
         * General endpoint, which always produces error.
         */
        public static final String FAIL_ENDPOINT = "/failPoint";
        /**
         * API endpoint, which always produces error.
         */
        public static final String FAIL_API_ENDPOINT = "/api/failPoint";
    }
}
