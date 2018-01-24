package ee.yals.test;


import ee.yals.YalsApplication;
import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.models.dao.SecretDao;
import ee.yals.models.dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Class contains methods, which apply to Test SuiteCase in Yals Application
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@TestPropertySource(locations = "classpath:test-app.properties")
public class YalsTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecretDao secretDao;


    @Before
    public void applyChangeSet3() {
        if (!userDao.findSingleByAlias(YalsApplication.YALS_GOD).isPresent()) {
            //Create YalsGod user
            User yalsGod = User.create(YalsApplication.YALS_GOD);
            userDao.save(yalsGod);
        }
        if (!userDao.findSingleByAlias("demo").isPresent()) {
            //Create demo user
            User demoUser = User.create("demo");
            userDao.save(demoUser);

            if (!secretDao.findSingleByUser(demoUser).isPresent()) {
                //Create demo password
                Secret demoSecret = Secret.create("demo").forUser(demoUser).please();
                secretDao.save(demoSecret);
            }
        }
    }

    @Test
    public void contextLoads() {

    }
}
