package pm.axe.exception.error;

import pm.axe.constants.App;
import pm.axe.constants.Header;
import pm.axe.constants.HttpCode;
import pm.axe.exception.AxeException;

import java.util.HashMap;

/**
 * Creates {@link AxeException#messageToUser} based on http status.
 *
 * @since 2.7
 */
public final class UserMessageGenerator {
    private static final HashMap<Integer, String> MAPPING = new HashMap<>();
    private static final String DEFAULT_MESSAGE = "General server error";

    static {
        MAPPING.put(App.NO_STATUS, DEFAULT_MESSAGE);
        MAPPING.put(HttpCode.BAD_REQUEST, "Got malformed request");
        MAPPING.put(HttpCode.NOT_FOUND, "Requested resource was not found at server");
        MAPPING.put(HttpCode.METHOD_NOT_ALLOWED, "API called with wrong method");
        MAPPING.put(HttpCode.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported MimeType, maybe " + Header.CONTENT_TYPE + " Header is missing");
        MAPPING.put(HttpCode.SERVER_ERROR, DEFAULT_MESSAGE);
        MAPPING.put(HttpCode.APP_IS_DOWN, "Application is down");
    }

    private UserMessageGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Provides user-friendly message based on http code.
     *
     * @param status int with http code
     * @return string with matched error message or {@link #DEFAULT_MESSAGE} is match not found.
     */
    public static String getMessageByStatus(final int status) {
        return MAPPING.getOrDefault(status, DEFAULT_MESSAGE);
    }
}
