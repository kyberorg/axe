package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.redis.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fallback DAO with local (app memory) storage for storing {@link UserSession}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class UserSessionLocalDao {
    private final Map<String, UserSession> storage = new HashMap<>();

    /**
     * Create or update {@link UserSession}.
     *
     * @param session object to store.
     */
    public void save(final UserSession session) {
        storage.put(session.getSessionId(), session);
    }

    /**
     * Retrieve {@link UserSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link UserSession} or not.
     */
    public Optional<UserSession> get(final String sessionId) {
        return Optional.ofNullable(storage.get(sessionId));
    }

    public void delete(final String sessionId) {
        storage.remove(sessionId);
    }
}
