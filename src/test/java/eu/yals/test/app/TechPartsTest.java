package eu.yals.test.app;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Testing Tech Parts and other non-standard locations
 *
 * @since 2.5.1
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TechPartsTest extends UnirestTest {

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";

    @Test
    public void isAcceptHeaderJsonAppReturnJsonWhenNothingFound() throws Exception {

        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertContentNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }
}
