package ee.yals.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Calculates host:port server running at
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public class HostHelper {

    private HostHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getHostFromRequest(HttpServletRequest request) {
        return request.getServerName() + ":" + request.getServerPort();
    }
}
