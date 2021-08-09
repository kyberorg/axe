package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory session storage.
 *
 * @since 3.2
 */
public class SessionBox {
    private static final Map<String, SessionBoxRecord> SESSIONS = new HashMap<>();

    private SessionBox() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Stores Vaadin Session if it is not already stored.
     *
     * @param vaadinSession current {@link VaadinSession} to store.
     */
    public static void storeSession(final VaadinSession vaadinSession) {
        if (vaadinSession == null || vaadinSession.getSession() == null) return;
        String sessionId = vaadinSession.getSession().getId();
        SESSIONS.putIfAbsent(sessionId, SessionBoxRecord.of(vaadinSession));
    }

    /**
     * Removes {@link VaadinSession} from {@link SessionBox}.
     *
     * @param vaadinSession stored {@link VaadinSession}.
     */
    public static void removeVaadinSession(final VaadinSession vaadinSession) {
        SESSIONS.remove(vaadinSession.getSession().getId());
    }

    /**
     * Removes stored {@link SessionBoxRecord} from {@link SessionBox}.
     *
     * @param sessionBoxRecord stored {@link SessionBoxRecord}
     */
    public static void removeRecord(final SessionBoxRecord sessionBoxRecord) {
        SESSIONS.remove(sessionBoxRecord.getHttpSession().getId());
    }

    static Map<String, SessionBoxRecord> getSessions() {
        return SESSIONS;
    }

}
