package ee.yals.services.user;

import ee.yals.models.User;

/**
 * Service which queries from DB about users
 *
 * @since 3.0
 */
public interface UserService {
    User getDefaultUser();
}
