package ee.yals.test.modeltests;

import ee.yals.models.Token;
import ee.yals.models.User;
import ee.yals.models.dao.TokenDao;
import ee.yals.models.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TokensTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    @Test
    public void canCreateToken() {
        User tokenOwner = createUser();
        Token token = Token.createFor(tokenOwner);
        String tokenValue = token.getToken();
        tokenDao.save(token);

        Optional<Token> foundToken = tokenDao.findSingleByToken(tokenValue);
        assertTrue(foundToken.isPresent());
        assertEquals(tokenValue, foundToken.get().getToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateTokenForNullUser() {
        Token.createFor(null);
        assertTrue("Assert, than token object cannot be created", false);
    }

    @Test
    public void ifTokenExpirationTimeInPastTokenNoLongerValid() throws InterruptedException {
        long tokenExpiresIn = 2; //in seconds
        System.setProperty(Token.TOKEN_LIFETIME_PROPERTY_KEY, Long.toString(tokenExpiresIn));

        User tokenOwner = createUser();
        Token token = Token.createFor(tokenOwner);
        tokenDao.save(token);

        Thread.sleep(tokenExpiresIn * 1000);

        Optional<Token> foundToken = tokenDao.findSingleByToken(token.getToken());
        if (foundToken.isPresent()) {
            assertTrue(foundToken.get().isExpired());
        } else {
            assertTrue("Token wasn't stored for some reason", false);
        }
    }

    @Test
    public void byDefaultNewTokenIsNotExpired() {
        User tokenOwner = createUser();
        Token token = Token.createFor(tokenOwner);
        tokenDao.save(token);

        Optional<Token> foundToken = tokenDao.findSingleByToken(token.getToken());
        if (foundToken.isPresent()) {
            assertFalse(foundToken.get().isExpired());
        } else {
            assertTrue("Token wasn't stored for some reason", false);
        }

    }

    private User createUser() {
        User user = User.create("Uzer");
        return userDao.save(user);
    }
}
