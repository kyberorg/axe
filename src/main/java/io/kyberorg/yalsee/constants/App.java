package io.kyberorg.yalsee.constants;

/**
 * Application constants.
 *
 * @since 2.3
 */
public final class App {
    public static final String EQUAL = "=";
    public static final String AND = "&";
    public static final String AT = "@";
    public static final String NO_VALUE = "_";
    public static final int NO_STATUS = -1;
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String WEB_NEW_LINE = "<BR>";
    public static final String URL_SAFE_SEPARATOR = ">>";
    public static final int THREE = 3;
    public static final int FOUR = 4;

    private App() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Emoji {
        public static final String WARNING = ":warning:";
        public static final String INFO = ":information_source:";
    }

    public static class Mattermost {
        public static final String BOT_ICON = "https://yals.ee/favicon.ico";
        public static final String BOT_NAME = "YalseeBot";
        public static final String SUPPORT_URL = "https://github.com/kyberorg/yalsee/issues";
    }

    public static class Properties {
        public static final String TELEGRAM_ENABLED = "telegram.enabled";
        public static final String TELEGRAM_TOKEN = "telegram.token";
        public static final String SERVER_URL = "server.url";
        public static final String SERVER_PORT = "server.port";
        public static final String PROXY_HOST = "http.proxyHost";
        public static final String PROXY_PORT = "http.proxyPort";
        public static final String APPLICATION_STAGE = "application.stage";
        public static final String DEV_MODE = "app.devMode";
        public static final String APP_SITE_TITLE = "app.site-title";
        public static final String SHORT_DOMAIN = "app.shortDomain";
        public static final String GA_ENABLED = "app.seo.ga.enabled";
        public static final String GA_FILE = "app.seo.ga.file";
        public static final String CRAWLERS_ALLOWED = "app.seo.robots.crawl-allowed";
        public static final String REDIRECT_PAGE_BYPASS_SYMBOL = "app.redirect-page.bypass-symbol";
        public static final String REDIRECT_PAGE_TIMEOUT = "app.redirect-page.timeout";
        public static final String SESSION_TIMEOUT = "app.session.timeout";
    }

    public static class Env {
        public static final String TELEGRAM_TOKEN = "TELEGRAM_TOKEN";
        public static final String SERVER_URL = "SERVER_URL";
        public static final String BUGSNAG_TOKEN = "BUGSNAG_TOKEN";
        public static final String DELETE_TOKEN = "DELETE_TOKEN";
    }

    public static class Git {
        public static final String REPOSITORY = "https://github.com/kyberorg/yalsee/commit";
    }

    public static class Params {
        public static final String ERROR_ID = "errorId";
    }

    public static class QR {
        public static final int DEFAULT_QR_BLOCK_SIZE = 371;
        public static final int DEFAULT_QR_CODE_SIZE = 350;
        public static final float QR_BLOCK_RATIO = 0.943f; // 350/371
        public static final int MINIMAL_SIZE_IN_PIXELS = 33;
    }

    public static class Defaults {
        public static final int REDIRECT_PAGE_TIMEOUT_SECONDS = 10;
        public static final int SESSION_TIMEOUT_SECONDS = 1800;
    }

    public static class Api {
        public static final String API_DOKS_URL = "https://app.swaggerhub.com/apis/kyberorg/Yalsee/1.0.1";
    }

    public static class Session {
        public static final int SESSION_WATCHDOG_INTERVAL = 20; //20 seconds
        public static final int SESSION_SYNC_INTERVAL = 5; //5 seconds
    }

    public static class CookieNames {
        public static final String YALSEE_SESSION = "YalseeSession";
    }
}
