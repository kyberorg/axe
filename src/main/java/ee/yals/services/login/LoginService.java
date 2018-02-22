package ee.yals.services.login;

import org.springframework.stereotype.Service;

/**
 * Useful methods for {@link ee.yals.controllers.rest.LoginRestController}
 *
 * @since 3.0
 */
@Service
public interface LoginService {

    boolean userNotValid(String username);

    boolean passwordNotValid(String plainPass, String username);

    boolean isTokenAlreadyExists(String token);
}
