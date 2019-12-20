package eu.yals.exception.error;

import java.util.HashMap;

import static eu.yals.constants.App.NO_STATUS;

public class UserMessageGenerator {
    private static HashMap<Integer, String> mapping = new HashMap<>();
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
