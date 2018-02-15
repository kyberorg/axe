package ee.yals.test.utiltests;

import ee.yals.core.TokenGenerator;
import org.apache.commons.validator.routines.RegexValidator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link TokenGenerator}
 *
 * @since 3.0
 */
public class TokenGeneratorTest {

    @Test
    public void tokenMustBeOnlyAlphabetic() {
        final String regexp = "[A-Za-z]*";
        final String token = TokenGenerator.generateNew();
        assertTrue("Token must be contain only letters. Got " + token, new RegexValidator(regexp).isValid(token));
    }
}