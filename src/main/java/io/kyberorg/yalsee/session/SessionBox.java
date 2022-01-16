package io.kyberorg.yalsee.session;

import io.kyberorg.yalsee.events.YalseeSessionCreatedEvent;
import io.kyberorg.yalsee.events.YalseeSessionDestroyedEvent;
import io.kyberorg.yalsee.redis.serializers.YalseeSessionGsonRedisSerializer;
import lombok.SneakyThrows;
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
    private static final Map<String, YalseeSession> SESSION_STORAGE = new HashMap<>();
    private static final Map<String, YalseeSession> PREVIOUS_VERSIONS = new HashMap<>();

    private static final YalseeSessionGsonRedisSerializer SERIALIZER = new YalseeSessionGsonRedisSerializer();

    private SessionBox() {
        throw new UnsupportedOperationException("Utility class");
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
     * Checks if {@link #PREVIOUS_VERSIONS} has given session.
     *
     * @param session session to search for.
     * @return true if {@link #PREVIOUS_VERSIONS} has given session, false if not.
     */
    static boolean hasPreviousVersion(final YalseeSession session) {
        if (session == null) return false;
        return PREVIOUS_VERSIONS.containsKey(session.getSessionId());
    }

    /**
     * Gets previous version of {@link YalseeSession} from {@link #PREVIOUS_VERSIONS} map.
     * It is good to use {@link #hasPreviousVersion(YalseeSession)} before to avoid {@code null}
     *
     * @param session session, previous version of which we retrieve.
     * @return previous version of {@link YalseeSession} or {@code null}
     * @throws IllegalArgumentException when session is null.
     * @see HashMap#get(Object)
     */
    static YalseeSession getPreviousVersion(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session is null");
        return PREVIOUS_VERSIONS.get(session.getSessionId());
    }

    /**
     * Sets given session as previous, so next check can get it using {@link #getPreviousVersion(YalseeSession)}.
     *
     * @param session valid {@link YalseeSession}.
     * @return same session as it param to make it work with Stream API.
     */
    @SneakyThrows
    static YalseeSession setAsPreviousVersion(final YalseeSession session) {
        byte[] json = SERIALIZER.serialize(session);
        YalseeSession prevSession = SERIALIZER.deserialize(json);
        if (prevSession != null) {
            PREVIOUS_VERSIONS.put(prevSession.getSessionId(), prevSession);
        }
        return session;
    }

    /**
     * Deletes session from {@link #PREVIOUS_VERSIONS} map.
     *
     * @param session session to delete.
     */
    static void deletePreviousVersion(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session is null");
        PREVIOUS_VERSIONS.remove(session.getSessionId());
    }
}
