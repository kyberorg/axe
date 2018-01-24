package ee.yals.services.token;

import ee.yals.models.User;

/**
 * Service which interacts with tokens
 *
 * @since 3.0
 */
public interface TokenService {

    String NO_TOKEN = "NOT_A_TOKEN";

    User giveUserFromToken(String token);
}
