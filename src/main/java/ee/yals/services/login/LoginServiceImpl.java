package ee.yals.services.login;

import ee.yals.models.Secret;
import ee.yals.models.Token;
import ee.yals.models.User;
import ee.yals.models.dao.SecretDao;
import ee.yals.models.dao.TokenDao;
import ee.yals.models.dao.UserDao;
import ee.yals.utils.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Std Implementation of {@link LoginService}
 *
 * @since 3.0
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecretDao secretDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private Password.Comparator passwordComparator;

    @Override
    public boolean userNotValid(String username) {
        Optional<User> user = userDao.findSingleByAlias(username);
        return !user.isPresent();
    }

    @SuppressWarnings("ConstantConditions") //Checked by userNotValid method
    @Override
    public boolean passwordNotValid(String plainPass, String username) {
        if (userNotValid(username)) return true;

        Optional<User> user = userDao.findSingleByAlias(username);
        Optional<Secret> secret = secretDao.findSingleByUser(user.get());
        if (!secret.isPresent()) {
            return true;
        }
        String actualPasswordString = secret.get().getPassword();

        Password.Status comparisionStatus = passwordComparator.comparePlain(plainPass).withEncrypted(actualPasswordString);
        return comparisionStatus == Password.NOT_EQUAL;
    }

    @Override
    public boolean isTokenAlreadyExists(String token) {
        Optional<Token> tokenOptional = tokenDao.findSingleByToken(token);
        return !tokenOptional.isPresent();
    }
}
