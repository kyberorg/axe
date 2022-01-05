package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.events.YalseeSessionCreatedEvent;
import io.kyberorg.yalsee.events.YalseeSessionDestroyedEvent;
import io.kyberorg.yalsee.session.YalseeSession;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory session storage.
 *
 * @since 3.2
 */
public final class SessionBox {
    private static final Map<String, SessionBoxRecord> SESSIONS = new HashMap<>();
    private static final Map<String, YalseeSession> sessionStorage = new HashMap<>();

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

    public static void storeSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        sessionStorage.putIfAbsent(yalseeSession.getSessionId(), yalseeSession);
        EventBus.getDefault().post(YalseeSessionCreatedEvent.createWith(yalseeSession));
    }

    public static void updateSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        sessionStorage.put(yalseeSession.getSessionId(), yalseeSession);
    }

    public static YalseeSession getSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return null;
        return sessionStorage.get(sessionId);
    }

    /**
     * Removes {@link VaadinSession} from {@link SessionBox}.
     *
     * @param vaadinSession stored {@link VaadinSession}.
     */
    public static void removeVaadinSession(final VaadinSession vaadinSession) {
        SESSIONS.remove(vaadinSession.getSession().getId());
    }

    public static void removeSession(final YalseeSession session) {
        if (session == null) return;
        sessionStorage.remove(session.getSessionId());
        EventBus.getDefault().post(YalseeSessionDestroyedEvent.createWith(session));
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

    public static Collection<YalseeSession> getAllSessions() {
        return sessionStorage.values();
    }
}
