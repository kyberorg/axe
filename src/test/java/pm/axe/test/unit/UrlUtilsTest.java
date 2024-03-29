package pm.axe.test.unit;

import org.junit.jupiter.api.Test;
import pm.axe.utils.UrlUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test suite for {@link UrlUtils}.
 *
 * @since 2.5
 */
public class UrlUtilsTest extends UnitTest {

    /**
     * Tests that decoding works.
     */
    @Test
    public void decodeRussianWikiUrl() {
        String encodedUrl = "https://ru.wikipedia.org/wiki/%D0%9E%D1%80%D0%B5%D1%81%D1%82";
        String exceptedResult = "https://ru.wikipedia.org/wiki/Орест";

        assertEquals(exceptedResult, UrlUtils.decodeUrl(encodedUrl));
    }

    /**
     * Tests that decoder doesn't damage link that needs no encoding, and it remains as is.
     */
    @Test
    public void decodeFullyLatinUrl() {
        String url = "https://axe.pm";
        String expectedResult = "https://axe.pm";

        assertEquals(expectedResult, UrlUtils.decodeUrl(url));
    }
}
