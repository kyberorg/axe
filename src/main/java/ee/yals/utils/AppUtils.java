package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * App-wide tools
 *
 * @since 1.0
 */
public class AppUtils {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

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

        public static String getHostFromRequest(HttpServletRequest request) {
            boolean isAppRunningInDocker = StringUtils.isNoneBlank((System.getProperty("docker.internal.port", "")));
            if (isAppRunningInDocker) {
                return "localhost" + ":" + System.getProperty("docker.internal.port");
            } else {
                return request.getServerName() + ":" + request.getServerPort();
            }
        }
    }
}
