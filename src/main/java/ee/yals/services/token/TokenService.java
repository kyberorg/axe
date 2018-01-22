package ee.yals.services.token;

import ee.yals.models.User;

/**
 * Class description
 *
 * @since 3.0
 */
public interface TokenService {

    User giveUserFromToken(String token);
}
