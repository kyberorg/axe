package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.yals.App;
import ee.yals.Env;
import org.apache.commons.lang3.StringUtils;

/**
 * App-wide tools
 *
 * @since 1.0
 */
public class AppUtils {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final String DUMMY_HOST = "DummyHost";
    private static final String DUMMY_TOKEN = "dummyToken";

    private AppUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates host:port server running at
     *
     * @since 2.0
     */
    public static class HostHelper {

        private HostHelper() {
            throw new UnsupportedOperationException("Utility class");
        }

        public static String getAPIHostPort() {
            return "localhost" + ":" + System.getProperty("server.port", "8080");
        }
    }

    public static String getServerUrl() {
        String serverUrl = System.getProperty(App.Properties.SERVER_URL, System.getenv(Env.SERVER_URL));
        return StringUtils.isNotBlank(serverUrl) ? serverUrl : DUMMY_HOST;
    }

    public static String getTelegramToken() {
        String token = System.getProperty(App.Properties.TELEGRAM_TOKEN, System.getenv(Env.TELEGRAM_TOKEN));
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }
}
