package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

public class SessionBox {
    private static final Map<String, VaadinSession> vaadinSessions = new HashMap<>();

    public static void storeSession(VaadinSession vaadinSession) {
        if (vaadinSession == null || vaadinSession.getSession() == null) return;
        String sessionId = vaadinSession.getSession().getId();
        if (vaadinSessions.containsKey(sessionId)) return;
        vaadinSessions.put(sessionId, vaadinSession);
    }

    public static void removeSession(VaadinSession vaadinSession) {
        vaadinSessions.remove(vaadinSession.getSession().getId());
    }

    static Map<String, VaadinSession> getVaadinSessions() {
        return vaadinSessions;
    }

}
