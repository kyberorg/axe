package ee.yals.test.utiltests;

import ee.yals.test.YalsTest;
import ee.yals.utils.Password;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Password.Encryptor}
 *
 * @since 3.0
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PasswordEncryptorTest extends YalsTest {

    @Autowired
    private Password.Encryptor encryptor;

    private static final int STD_SHA_512_LENGTH = 128;

    @Test
    public void emptyPasswordForNullString() {
        String enc = encryptor.encrypt(null);
        assertTrue(StringUtils.isBlank(enc));
    }

    @Test
    public void emptyPasswordForEmptyString() {
        String enc = encryptor.encrypt("");
        assertTrue(StringUtils.isBlank(enc));
    }

    @Test
    public void validPasswordForValidString() {
        String plain = "demo";
        String enc = encryptor.encrypt(plain);
        assertTrue(StringUtils.isNotBlank(enc));
        assertTrue(enc.length() == STD_SHA_512_LENGTH);
    }
}
