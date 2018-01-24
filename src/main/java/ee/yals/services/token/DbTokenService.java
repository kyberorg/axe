package ee.yals.services.token;

import ee.yals.models.Token;
import ee.yals.models.User;
import ee.yals.models.dao.TokenDao;
import ee.yals.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Service which queries from DB about tokens
 *
 * @since 3.0
 */
@Qualifier("dbTokenService")
@Component
public class DbTokenService implements TokenService {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenDao tokenDao;

    @Override
    public User giveUserFromToken(String token) {
        if (token.equals(NO_TOKEN)) {
            //default user
            return userService.getDefaultUser();
        } else {
            Optional<Token> tokenRecord = tokenDao.findSingleByToken(token);
            if (tokenRecord.isPresent()) {
                return tokenRecord.get().getOwner();
            } else {
                return userService.getDefaultUser();
            }
        }
    }
}
