package pm.axe.test.app;

import pm.axe.constants.Header;
import pm.axe.constants.HttpCode;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import pm.axe.Endpoint;
import pm.axe.test.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for /{ident}.
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class SlashEndpointTest extends UnirestTest {
    public static final String TAG = "[" + SlashEndpointTest.class.getSimpleName() + "]";

    /**
     * Tests that stored URL redirects to original long URL.
     */
    @Test
    public void storeURLAndCheckIfRedirectToSameURL() {
        String url = "https://ci.yadev.eu";
        String ident = store(url);
        assertNotNull(ident);

        Unirest.config().reset().followRedirects(false);

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.SLASH_BASE + ident + TestUtils.addRedirectPageBypassSymbol());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        Unirest.config().reset().followRedirects(true);

        assertEquals(HttpCode.TEMPORARY_REDIRECT, result.getStatus());
        assertTrue(result.getHeaders().containsKey(Header.LOCATION));
        String location = result.getHeaders().getFirst(Header.LOCATION);
        assertTrue(StringUtils.isNotBlank(location), "Got empty " + Header.LOCATION + " header");
    }

    /**
     * Request something that not exists = 404.
     */
    @Test
    public void requestWithIdentThatNotStoredGivesStatus404() {
        String ident = "habaHaba";

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.SLASH_BASE + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpCode.NOT_FOUND, result.getStatus());
    }

}