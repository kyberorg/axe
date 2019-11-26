package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.json.EmptyJson;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit testing for store API
 *
 * @since 1.0
 */
public class StoreApiTest extends UnirestTest {

    @Test
    public void onRequestWithoutBodyStatusIs400() {
        HttpResponse<String> result = uniPost(Endpoint.STORE_API, null);
        assertNotNull(result);
        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestWithEmptyBodyStatusIs400() {
        HttpResponse<String> result = uniPost(Endpoint.STORE_API, "");
        assertNotNull(result);
        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestWithNonJsonBodyStatusIs421() {
        HttpResponse<String> result = uniPost(Endpoint.STORE_API, "not a JSON");
        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs421() {
        HttpResponse<String> result = uniPost(Endpoint.STORE_API, EmptyJson.create().toString());
        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithEmptyLinkStatusIs421() {
        String longLink = "";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, correctJson);
        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithNotALinkStatusIs421() {
        String longLink = "not a Link";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, correctJson);
        assertNotNull(result);
        assertEquals(421, result.getStatus());

    }

    @Test
    public void onRequestWithCorrectLinkStatusIs201() {
        String longLink = "http://virtadev.net"; //That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, correctJson);
        assertNotNull(result);
        assertEquals(201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithIdent() {
        String longLink = "http://virtadev.net"; //That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, correctJson);
        assertNotNull(result);
        assertEquals(201, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson;
        try {
            replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        } catch (Exception e) {
            fail("Could not parse reply JSON");
            return;
        }

        assertNotNull(replyJson);
        assertNotNull(replyJson.getIdent());
    }

    @Test
    public void onRequestWithLinkWithoutProtocolStatusIs201() {
        String linkWithoutProtocol = "github.com/yadevee/yals/issues/50";
        String correctJson = StoreRequestJson.create().withLink(linkWithoutProtocol).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, correctJson);
        assertNotNull(result);
        assertEquals(201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

}
