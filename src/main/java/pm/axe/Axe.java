package pm.axe;

/**
 * Application constants.
 *
 * @since 2.3
 */
public final class Axe {
    private Axe() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Constants.
     */
    public static class C {
        public static final String ALL_MIME_TYPES = "*/*";
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
        public static final int ONE_SECOND_IN_MILLIS = 1000;
        public static final String TIME_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss z";
        public static final String MINUS = "-";
    }

    public static class Defaults {
        public static final int REDIRECT_PAGE_TIMEOUT_SECONDS = 10;
        public static final int SESSION_TIMEOUT_SECONDS = 1800;
        public static final int NOTIFICATION_DURATION_MILLIS = 1500;
        public static final String EMAIL_FROM_ADDRESS = "axe@axe.pm";
        public static final long NO_USER = -1;
    }

    public static class Profiles {
        public static final String ACTUATOR = "actuator";
        public static final String PROXY = "proxy";
    }

    public static class Params {
        public static final String ERROR_ID = "errorId";
        public static final String TOKEN = "token";
    }

    public static class Properties {
        public static final String APPLICATION_NAME = "spring.application.name";
        public static final String TELEGRAM_ENABLED = "app.telegram.enabled";
        public static final String TELEGRAM_TOKEN = "app.telegram.token";
        public static final String TELEGRAM_BOT_NAME = "app.telegram.bot-name";
        public static final String SERVER_URL = "app.server.url";
        public static final String SERVER_PORT = "server.port";
        public static final String PROXY_HOST = "http.proxyHost";
        public static final String PROXY_PORT = "http.proxyPort";
        public static final String APPLICATION_STAGE = "app.stage";
        public static final String DEV_MODE = "app.dev-mode";
        public static final String APP_SITE_TITLE = "app.site.title";
        public static final String SHORT_DOMAIN = "app.server.short-domain";
        public static final String CRAWLERS_ALLOWED = "app.seo.robots.crawl-allowed";
        public static final String REDIRECT_PAGE_BYPASS_SYMBOL = "app.redirect-page.bypass-symbol";
        public static final String REDIRECT_PAGE_TIMEOUT = "app.redirect-page.timeout";
        public static final String SESSION_TIMEOUT = "app.session.timeout";
        public static final String FACEBOOK_APP_ID = "app.facebook.app-id";
        public static final String SERVER_KEY = "app.security.server-key";
        public static final String PASSWORD_SALT = "app.security.password-salt";
        public static final String EMAIL_FOR_ERRORS = "app.mail.email4errors";
        public static final String EMAIL_FROM_ADDRESS = "app.mail.from-address";
        public static final String SHOW_RENAME_NOTIFICATION = "app.show-rename-notification";
    }

    public static class Envs {
        public static final String TELEGRAM_TOKEN = "TELEGRAM_TOKEN";
        public static final String SERVER_URL = "SERVER_URL";
        public static final String BUGSNAG_TOKEN = "BUGSNAG_TOKEN";
        public static final String MASTER_TOKEN = "MASTER_TOKEN";
    }

    public static class Headers {
        public static final String LOCATION = "Location";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String ACCEPT = "Accept";
        public static final String X_DEVELOPER = "X-Developer";
        public static final String X_AXE_TOKEN = "X-Axe-Token";
        public static final String X_REAL_IP = "X-Real-IP";
        public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    }

    public static class CookieNames {
        public static final String AXE_SESSION = "AxeSession";
        public static final String STATS_BANNER_COOKIE = "AxeStatsBannerShown";
    }

    public static class Emoji {
        public static final String WARNING = ":warning:";
        public static final String INFO = ":information_source:";
    }

    public static class Mattermost {
        public static final String BOT_ICON = "https://axe.pm/favicon.ico";
        public static final String BOT_NAME = "AxeBot";
        public static final String SUPPORT_URL = "https://github.com/kyberorg/axe/issues";
    }

    public static class Telegram {
        public static final String TELEGRAM_URL = "https://telegram.me/";
        public static final String KYBERORG = "@kyberorg";
    }

    public static class Git {
        public static final String REPOSITORY = "https://github.com/kyberorg/axe/commit";
    }

    public static class QR {
        public static final int DEFAULT_QR_BLOCK_SIZE = 371;
        public static final int DEFAULT_QR_CODE_SIZE = 350;
        public static final float QR_BLOCK_RATIO = 0.943f; // 350/371
        public static final int MINIMAL_SIZE_IN_PIXELS = 33;
    }

    public static class Api {
        public static final String API_DOKS_URL = "https://app.swaggerhub.com/apis/kyberorg/Axe/2.0.0-oas3";
    }

    public static class Session {
        public static final int SESSION_WATCHDOG_INTERVAL = 20; //20 seconds
        public static final int SESSION_SYNC_INTERVAL = 5; //5 seconds
        public static final String EMPTY_ID = "";
    }
}
