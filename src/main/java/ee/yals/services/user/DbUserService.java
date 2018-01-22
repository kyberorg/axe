package ee.yals.services.user;

import ee.yals.YalsApplication;
import ee.yals.models.User;
import ee.yals.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Service which queries from DB about users
 *
 * @since 3.0
 */
@Qualifier("dbUserService")
@Component
public class DbUserService implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getDefaultUser() {
        Optional<User> godUser = userDao.findSingleByAlias(YalsApplication.YALS_GOD);
        if (godUser.isPresent()) {
            return godUser.get();
        } else {
            throw new Error("Application initialization error. Reason: '" + YalsApplication.YALS_GOD + "'" +
                    " user is not found in DB");
        }
    }
}
