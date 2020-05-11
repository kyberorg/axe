package eu.yals.exception.error;

import java.util.HashMap;

import static eu.yals.constants.App.NO_STATUS;
import static eu.yals.constants.HttpCode.*;

/**
 * Creates {@link eu.yals.exception.YalsException#messageToUser} based on http status.
 *
 * @since 2.7
 */
public final class UserMessageGenerator {
    private static final HashMap<Integer, String> MAPPING = new HashMap<>();
    private static final String DEFAULT_MESSAGE = "General server error";

    static {
        MAPPING.put(NO_STATUS, DEFAULT_MESSAGE);
        MAPPING.put(STATUS_400, "Got malformed request");
        MAPPING.put(STATUS_404, "Requested resource was not found at server");
        MAPPING.put(STATUS_405, "API called with wrong method");
        MAPPING.put(STATUS_500, DEFAULT_MESSAGE);
        MAPPING.put(STATUS_503, "Application is down");
    }

    private UserMessageGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Provides user friendly message based on http code.
     *
     * @param status int with http code
     * @return string with matched error message or {@link #DEFAULT_MESSAGE} is match not found.
     */
    public static String getMessageByStatus(final int status) {
        return MAPPING.getOrDefault(status, DEFAULT_MESSAGE);
    }
}
