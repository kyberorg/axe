package io.kyberorg.yalsee.exception.error;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.YalseeException;

import java.util.HashMap;

/**
 * Creates {@link YalseeException#messageToUser} based on http status.
 *
 * @since 2.7
 */
public final class UserMessageGenerator {
    private static final HashMap<Integer, String> MAPPING = new HashMap<>();
    private static final String DEFAULT_MESSAGE = "General server error";

    static {
        MAPPING.put(App.NO_STATUS, DEFAULT_MESSAGE);
        MAPPING.put(HttpCode.STATUS_400, "Got malformed request");
        MAPPING.put(HttpCode.STATUS_404, "Requested resource was not found at server");
        MAPPING.put(HttpCode.STATUS_405, "API called with wrong method");
        MAPPING.put(HttpCode.STATUS_415, "Unsupported MimeType, maybe "+ Header.CONTENT_TYPE+" Header is missing");
        MAPPING.put(HttpCode.STATUS_500, DEFAULT_MESSAGE);
        MAPPING.put(HttpCode.STATUS_503, "Application is down");
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
