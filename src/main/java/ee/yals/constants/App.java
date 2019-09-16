package ee.yals.constants;

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

    public static class Emoji {
        private Emoji() {
        }

        public static final String WARNING = ":warning:";
        public static final String INFO = ":information_source:";
    }

    public static class Properties {
        public static final String TELEGRAM_TOKEN = "telegram.token";
        public static final String SERVER_URL = "server.url";
        public static final String SERVER_PORT = "server.port";
        public static final String TEST_URL = "testUrl";
    }

    public static class Env {
        public static final String TELEGRAM_TOKEN = "TELEGRAM_TOKEN";
        public static final String SERVER_URL = "SERVER_URL";
    }

}
