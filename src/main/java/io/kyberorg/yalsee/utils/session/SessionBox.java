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
    private static final Map<String, YalseeSession> SESSION_STORAGE = new HashMap<>();

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
     * Stores new session. It will be stored only if not existed previously.
     * Also fires {@link YalseeSessionCreatedEvent}.
     *
     * @param yalseeSession non null session to store.
     */
    public static void storeSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        SESSION_STORAGE.putIfAbsent(yalseeSession.getSessionId(), yalseeSession);
        EventBus.getDefault().post(YalseeSessionCreatedEvent.createWith(yalseeSession));
    }

    /**
     * Checks if {@link SessionBox} has {@link YalseeSession} with given ID or not.
     *
     * @param sessionId non-empty string with session id to check.
     * @return true if {@link YalseeSession} with given ID is found, false - if not.
     */
    public static boolean hasSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return false;
        return SESSION_STORAGE.containsKey(sessionId);
    }

    /**
     * Gets Stored session by its ID.
     *
     * @param sessionId non-empty string with Session identifier.
     * @return {@link YalseeSession} stored or {@code null} if not found or provided sessionId is blank.
     */
    public static YalseeSession getSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return null;
        return SESSION_STORAGE.get(sessionId);
    }

    /**
     * Get all stored sessions.
     *
     * @return collection of currently stored {@link YalseeSession}.
     */
    public static Collection<YalseeSession> getAllSessions() {
        return SESSION_STORAGE.values();
    }

    /**
     * Updates stored session.
     *
     * @param yalseeSession session to update.
     */
    public static void updateSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        SESSION_STORAGE.put(yalseeSession.getSessionId(), yalseeSession);
    }

    /**
     * Removes provided {@link YalseeSession} from {@link SessionBox}.
     *
     * @param session stored {@link YalseeSession} to remove.
     */
    public static void removeSession(final YalseeSession session) {
        if (session == null) return;
        SESSION_STORAGE.remove(session.getSessionId());
        EventBus.getDefault().post(YalseeSessionDestroyedEvent.createWith(session));
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
