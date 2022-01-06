package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.session.SessionBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Fallback DAO with local (app memory) storage for storing {@link YalseeSession}.
 * See {@link SessionBox}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class YalseeSessionLocalDao {

    /**
     * Stores new {@link YalseeSession}.
     * .
     *
     * @param session object to store.
     */
    public void create(final YalseeSession session) {
        SessionBox.storeSession(session);
    }

    /**
     * Updates existing {@link YalseeSession}.
     *
     * @param session stored object to update.
     */
    public void update(final YalseeSession session) {
        SessionBox.updateSession(session);
    }

    /**
     * Retrieves {@link YalseeSession} by its Id.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link YalseeSession} or not.
     */
    public Optional<YalseeSession> get(final String sessionId) {
        return Optional.ofNullable(SessionBox.getSession(sessionId));
    }

    /**
     * Retrieves all stored {@link YalseeSession} objects.
     *
     * @return collection of elements stored at {@link SessionBox}
     */
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
