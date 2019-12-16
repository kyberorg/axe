package eu.yals.exception.error;

import eu.yals.utils.ErrorUtils;

import java.util.HashMap;

public class UserMessageGenerator {
    private static HashMap<Integer, String> mapping = new HashMap<>();
    private static final String DEFAULT_MESSAGE = "General server error";

    static {
        mapping.put(ErrorUtils.Args.NO_STATUS, DEFAULT_MESSAGE);
        mapping.put(400, "Got malformed request");
        mapping.put(404, "Requested resource was not found at server");
        mapping.put(500, DEFAULT_MESSAGE);
        mapping.put(503, "Application is down");
    }

    public static String getMessageByStatus(int status) {
        return mapping.getOrDefault(status, DEFAULT_MESSAGE);
    }
}
