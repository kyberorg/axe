package ee.yals.test.modeltests;

import ee.yals.models.User;
import ee.yals.models.dao.UserDao;
import ee.yals.test.YalsTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-app.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserTest extends YalsTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void canCreateNewUser() {
        String userName = "uzer";
        createUzer(userName);

        Optional<User> foundUser = userDao.findSingleByAlias(userName);

        assertTrue(foundUser.isPresent());
        assertEquals(userName, foundUser.get().getAlias());
    }

    @Test
    public void canUpdateNewUser() {
        String userName = "uzer";
        User uzer = createUzer(userName);

        String newUserName = "uzerUpd";
        uzer.updateAliasWith(newUserName);
        userDao.save(uzer);

        Optional<User> foundUser = userDao.findSingleByAlias(newUserName);
        assertTrue(foundUser.isPresent());
        assertEquals(newUserName, foundUser.get().getAlias());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void cannotCreateUserWithSameAlias() {
        String userName = "uzer";
        createUzer(userName);

        User uzer2 = User.create(userName);
        userDao.save(uzer2);
    }

    @Test
    public void createdSameAsUpdatedForNewUser() {
        User createdUser = createUzer("uzer");

        long created = createdUser.getCreated();
        long updated = createdUser.getUpdated();

        assertTrue("Created Time is not same as updated for newly created user ", created == updated);
    }

    @Test
    public void updatedFieldUpdatesWhenUpdatingUser() {
        User createdUser = createUzer("uzer");
        long updatedOfCreatedUser = createdUser.getUpdated();

        createdUser.updateAliasWith("newUzer");
        User updatedUser = userDao.save(createdUser);

        long updatedAfterUpdate = updatedUser.getUpdated();

        assertFalse(updatedOfCreatedUser == updatedAfterUpdate);
    }

    @Test
    public void createdFieldStaySameWhenUpdatingUser() {
        User createdUser = createUzer("uzer");
        long createdOfCreatedUser = createdUser.getCreated();

        createdUser.updateAliasWith("newUzer");
        User updatedUser = userDao.save(createdUser);

        long createdAfterUpdate = updatedUser.getCreated();

        assertEquals(createdAfterUpdate, createdOfCreatedUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateUserWithEmptyAlias() {
        createUzer("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateUserWithNullAlias() {
        createUzer(null);
    }

    private User createUzer(String userName) {
        User uzer = User.create(userName);
        return userDao.save(uzer);
    }
}
