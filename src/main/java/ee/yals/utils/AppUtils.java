package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.yals.Env;
import org.apache.commons.lang3.StringUtils;

/**
 * App-wide tools
 *
 * @since 1.0
 */
public class AppUtils {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    public static final String BR = System.getProperty("line.separator");

    private AppUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates host:port server running at
     *
     * @since 2.0
     */
    public static class HostHelper {
        private static final String DUMMY_HOST = "DummyHost";

        private HostHelper() {
            throw new UnsupportedOperationException("Utility class");
        }

        public static String getAPIHostPort() {
            return "localhost" + ":" + System.getProperty("server.port", "8080");
        }

        public static String getServerUrl() {
            String env = System.getenv(Env.SERVER_URL);
            return StringUtils.isNotBlank(env) ? env : DUMMY_HOST;
        }
    }
}
