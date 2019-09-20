package eu.yals.test.unit;

import eu.yals.utils.AppUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link AppUtils}
 *
 * @since 2.5
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppUtilsTest {

    @Autowired
    private AppUtils appUtils;

    @Test
    public void decodeRussianWikiUrl() {
        String encodedUrl = "https://ru.wikipedia.org/wiki/%D0%9E%D1%80%D0%B5%D1%81%D1%82";
        String exceptedResult = "https://ru.wikipedia.org/wiki/Орест";

        assertEquals(exceptedResult, appUtils.decodeUrl(encodedUrl));
    }

    @Test
    public void decodeFullyLatinUrl() {
        String url = "https://yals.ee";
        String expectedResult = "https://yals.ee";

        assertEquals(expectedResult, appUtils.decodeUrl(url));
    }
}