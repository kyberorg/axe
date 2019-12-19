package eu.yals.constants;

/**
 * Application constants
 *
 * @since 2.3
 */
public class App {
    private App() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String EQUAL = "=";
    public static final String AND = "&";
    public static final String AT = "@";
    public static final String NO_VALUE = "_";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String WEB_NEW_LINE = "<BR>";

    public static class Emoji {
        public static final String WARNING = ":warning:";
        public static final String INFO = ":information_source:";
    }

    public static class Mattermost {
        public static final String BOT_ICON = "https://yals.eu/favicon.ico";
        public static final String BOT_NAME = "YalsBot";
        public static final String SUPPORT_URL = "https://github.com/yadevee/yals/issues";
    }

    public static class Properties {
        public static final String TELEGRAM_ENABLED = "telegram.enabled";
        public static final String TELEGRAM_TOKEN = "telegram.token";
        public static final String SERVER_URL = "server.url";
        public static final String SERVER_PORT = "server.port";
        public static final String PROXY_HOST = "http.proxyHost";
        public static final String PROXY_PORT = "http.proxyPort";
        public static final String APPLICATION_STAGE = "application.stage";
    }

    public static class Env {
        public static final String TELEGRAM_TOKEN = "TELEGRAM_TOKEN";
        public static final String SERVER_URL = "SERVER_URL";
        public static final String BUGSNAG_TOKEN = "BUGSNAG_TOKEN";
    }

    public static class Git {
        public static final String REPOSITORY = "https://github.com/yadevee/yals/commit";
    }

    public static class Params {
        public static final String ERROR_ID = "errorId";
    }
}
