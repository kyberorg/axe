package pm.axe.db.dao;

import pm.axe.db.dao.base.TimeAwareCrudDao;
import pm.axe.db.models.User;

import java.util.Optional;

/**
 * DAO for {@link User} table.
 */
public interface UserDao extends TimeAwareCrudDao<User, Long> {
    /**
     * Finds {@link User} by its {@link User#username}.
     *
     * @param username string with username to search.
     * @return {@link Optional} with {@link User} record inside or {@link Optional#empty()}.
     */
    Optional<User> findByUsername(String username);
}
