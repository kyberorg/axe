package ee.yals.test.utiltests;

import ee.yals.test.YalsTest;
import ee.yals.utils.Password;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static ee.yals.utils.Password.EQUAL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test set for {@link Password.Comparator}
 *
 * @since 3.0
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PasswordComparatorTest extends YalsTest {

    @Autowired
    private Password.Comparator comparator;

    @Autowired
    private Password.Encryptor encryptor;

    @Test
    public void passwordShouldBeEqualOnlyToSamePassword() {
        String passA = "abc";
        String passB = "def";
        String passEmpty = "";
        String passNull = null;

        String encryptedA = encryptor.encrypt(passA);

        assertTrue(comparator.comparePlain(passA).withEncrypted(encryptedA) == EQUAL);
        assertFalse(comparator.comparePlain(passB).withEncrypted(encryptedA) == EQUAL);
        assertFalse(comparator.comparePlain(passEmpty).withEncrypted(encryptedA) == EQUAL);
        assertFalse(comparator.comparePlain(passNull).withEncrypted(encryptedA) == EQUAL);

    }

    @Test
    public void differentPasswordShouldNotBeEqual() {
        String passA = "abc";
        String passB = "def";
        String passEmpty = "";
        String passNull = null;

        String encryptedB = encryptor.encrypt(passB);

        assertFalse(comparator.comparePlain(passA).withEncrypted(encryptedB) == EQUAL);
        assertTrue(comparator.comparePlain(passB).withEncrypted(encryptedB) == EQUAL);
        assertFalse(comparator.comparePlain(passEmpty).withEncrypted(encryptedB) == EQUAL);
        assertFalse(comparator.comparePlain(passNull).withEncrypted(encryptedB) == EQUAL);
    }

    @Test
    public void emptyPasswordShouldBeEqualToEmptyAndNullPasswordOnly() {
        String passA = "abc";
        String passB = "def";
        String passEmpty = "";
        String passNull = null;

        String encryptedEmpty = encryptor.encrypt(passEmpty);

        assertFalse(comparator.comparePlain(passA).withEncrypted(encryptedEmpty) == EQUAL);
        assertFalse(comparator.comparePlain(passB).withEncrypted(encryptedEmpty) == EQUAL);
        assertTrue(comparator.comparePlain(passEmpty).withEncrypted(encryptedEmpty) == EQUAL);
        assertTrue(comparator.comparePlain(passNull).withEncrypted(encryptedEmpty) == EQUAL);
    }

    @Test
    public void nullPasswordShouldBeEqualToEmptyPasswordOnly() {
        String passA = "abc";
        String passB = "def";
        String passEmpty = "";
        String passNull = null;

        String encryptedNull = encryptor.encrypt(passNull);

        assertFalse(comparator.comparePlain(passA).withEncrypted(encryptedNull) == EQUAL);
        assertFalse(comparator.comparePlain(passB).withEncrypted(encryptedNull) == EQUAL);
        assertTrue(comparator.comparePlain(passEmpty).withEncrypted(encryptedNull) == EQUAL);
        assertTrue(comparator.comparePlain(passNull).withEncrypted(encryptedNull) == EQUAL);
    }
}
