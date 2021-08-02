package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

public class SessionBox {
    private static final Map<String, Sessions> sessions = new HashMap<>();

    public static void storeSession(final VaadinSession vaadinSession) {
        if (vaadinSession == null || vaadinSession.getSession() == null) return;
        String sessionId = vaadinSession.getSession().getId();
        if (sessions.containsKey(sessionId)) return;
        sessions.put(sessionId, Sessions.of(vaadinSession));
    }

    public static void removeVaadinSession(final VaadinSession vaadinSession) {
        sessions.remove(vaadinSession.getSession().getId());
    }

    public static void removeRecord(final Sessions sessionsRecord) {
        sessions.remove(sessionsRecord.getHttpSession().getId());
    }

    static Map<String, Sessions> getSessions() {
        return sessions;
    }

}
