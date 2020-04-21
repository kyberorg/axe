package eu.yals.exception.error;

import eu.yals.exception.YalsException;

import java.util.HashMap;

import static eu.yals.constants.App.NO_STATUS;

/**
 * Creates {@link YalsException#messageToUser} based on http status
 *
 * @since 2.7
 */
public class UserMessageGenerator {
    private static final HashMap<Integer, String> mapping = new HashMap<>();
    private static final String DEFAULT_MESSAGE = "General server error";

    static {
        mapping.put(NO_STATUS, DEFAULT_MESSAGE);
        mapping.put(400, "Got malformed request");
        mapping.put(404, "Requested resource was not found at server");
        mapping.put(500, DEFAULT_MESSAGE);
        mapping.put(503, "Application is down");
    }

    public static String getMessageByStatus(int status) {
        return mapping.getOrDefault(status, DEFAULT_MESSAGE);
    }
}
