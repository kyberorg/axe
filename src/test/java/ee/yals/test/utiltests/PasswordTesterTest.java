package ee.yals.test.utiltests;

import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.models.dao.SecretDao;
import ee.yals.models.dao.UserDao;
import ee.yals.test.YalsTest;
import ee.yals.utils.Password;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static ee.yals.utils.Password.EQUAL;
import static ee.yals.utils.Password.NOT_EQUAL;
import static org.junit.Assert.assertTrue;

/**
 * Tests cases for {@link Password.Tester}
 *
 * @since 3.0
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PasswordTesterTest extends YalsTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecretDao secretDao;

    @Autowired
    private Password.Tester tester;

    @Test
    public void validPasswordGivesPositiveCheckResult() {
        String password = "myPassword";
        User testUser = createUserWith(password);

        Password.Status status = tester.test(password).forUser(testUser);
        assertTrue(status == EQUAL);
    }

    @Test
    public void notValidPasswordGivesNegativeCheckResult() {
        String password = "myPassword";
        String wrongPassword = "wrongPassword";
        User testUser = createUserWith(password);

        Password.Status status = tester.test(wrongPassword).forUser(testUser);
        assertTrue(status == NOT_EQUAL);
    }

    @Test
    public void forUserWithoutSecretResultAlwaysNegative() {
        String emptyPass = "";
        User testUser = createUserWithoutSecret();
        Password.Status status = tester.test(emptyPass).forUser(testUser);
        assertTrue(status == NOT_EQUAL);
    }

    private User createUserWith(String password) {
        User simpleUser = User.create("testUser");
        userDao.save(simpleUser);
        Secret secret = Secret.create(password).forUser(simpleUser).please();
        secretDao.save(secret);
        return simpleUser;
    }

    private User createUserWithoutSecret() {
        User simpleUser = User.create("testUser");
        return userDao.save(simpleUser);
    }
}
