package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.redis.YalseeSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fallback DAO with local (app memory) storage for storing {@link YalseeSession}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class YalseeSessionLocalDao {
    private final Map<String, YalseeSession> storage = new HashMap<>();

    /**
     * Create or update {@link YalseeSession}.
     *
     * @param session object to store.
     */
    public void save(final YalseeSession session) {
        storage.put(session.getSessionId(), session);
    }

    /**
     * Retrieve {@link YalseeSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link YalseeSession} or not.
     */
    public Optional<YalseeSession> get(final String sessionId) {
        return Optional.ofNullable(storage.get(sessionId));
    }

    public void delete(final String sessionId) {
        storage.remove(sessionId);
    }
}
