package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.session.SessionBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Fallback DAO with local (app memory) storage for storing {@link YalseeSession}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class YalseeSessionLocalDao {

    public void create(final YalseeSession session) {
        SessionBox.storeSession(session);
    }

    /**
     * Update {@link YalseeSession}.
     *
     * @param session object to store.
     */
    public void update(final YalseeSession session) {
        SessionBox.updateSession(session);
    }

    /**
     * Retrieve {@link YalseeSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link YalseeSession} or not.
     */
    public Optional<YalseeSession> get(final String sessionId) {
        return Optional.ofNullable(SessionBox.getSession(sessionId));
    }

    public Collection<YalseeSession> getAllSessions() {
        return SessionBox.getAllSessions();
    }

    /**
     * Deletes object if it exists.
     *
     * @param session session object to delete.
     */
    public void delete(final YalseeSession session) {
        SessionBox.removeSession(session);
    }
}
