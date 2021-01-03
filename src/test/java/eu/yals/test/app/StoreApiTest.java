package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.json.EmptyJson;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing for store API
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class StoreApiTest extends UnirestTest {
    public static final String TAG = "[" + StoreApiTest.class.getSimpleName() + "]";

    @Test
    public void onRequestWithoutBodyStatusIs400() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestWithEmptyBodyStatusIs400() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body("");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestWithNonJsonBodyStatusIs421() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body("not a JSON");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs421() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(EmptyJson.create().toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    @Test
    public void onRequestWithEmptyLinkStatusIs421() {
        String longLink = "";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    @Test
    public void onRequestWithNotALinkStatusIs421() {
        String longLink = "not a Link";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(421, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    @Test
    public void onRequestWithCorrectLinkStatusIs201() {
        String longLink = "http://virtadev.net"; // That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithIdent() {
        String longLink = "http://virtadev.net"; // That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

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

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }
}
