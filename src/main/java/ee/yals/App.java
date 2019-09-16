package ee.yals;

/**
 * App Constants
 *
 * @since 2.5
 */
public class App {
    private App() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Properties {
        public static final String TELEGRAM_TOKEN = "telegram.token";
        public static final String SERVER_URL = "server.url";
        public static final String TEST_URL = "testUrl";
    }

    public static class Env {
        public static final String TELEGRAM_TOKEN = "TELEGRAM_TOKEN";
        public static final String SERVER_URL = "SERVER_URL";
    }
}
