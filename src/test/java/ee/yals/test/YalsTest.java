package ee.yals.test;


import ee.yals.YalsApplication;
import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.models.dao.SecretDao;
import ee.yals.models.dao.UserDao;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class cointains methods, which apply to Test SuiteCase in Yals Application
 */
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
                //TODO real function here
                //Create demo password
                Secret demoSecret = Secret.create("26C669CD0814AC40E5328752B21C4AA6450D16295E4EEC30356A06A911C23983AAEBE12D5DA38EEEBFC1B213BE650498DF8419194D5A26C7E0A50AF156853C79")
                        .forUser(demoUser)
                        .please();
                secretDao.save(demoSecret);
            }
        }
    }
}
