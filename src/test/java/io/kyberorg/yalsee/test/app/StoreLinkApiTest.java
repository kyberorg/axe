package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.json.EmptyJson;
import io.kyberorg.yalsee.json.StoreRequestJson;
import io.kyberorg.yalsee.json.StoreResponseJson;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static io.kyberorg.yalsee.constants.Header.CONTENT_TYPE;
import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing for store API.
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class StoreLinkApiTest extends UnirestTest {
    public static final String TAG = "[" + StoreLinkApiTest.class.getSimpleName() + "]";

    /**
     * Request without body = 400.
     */
    @Test
    public void onRequestWithoutBodyStatusIs400() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
    }

    /**
     * Request with empty body = 400.
     */
    @Test
    public void onRequestWithEmptyBodyStatusIs400() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body("");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
    }

    /**
     * Request with not JSON body = 400.
     */
    @Test
    public void onRequestWithNonJsonBodyStatusIs400() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body("not a JSON");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with JSON Body without link inside = 422.
     */
    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs422() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.LINKS_API).body(EmptyJson.create().toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_422, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with JSON Body with empty link inside = 422.
     */
    @Test
    public void onRequestWithEmptyLinkStatusIs422() {
        String longLink = "";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_422, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with JSON Body with non valid link inside = 422.
     */
    @Test
    public void onRequestWithNotALinkStatusIs422() {
        String longLink = "not a Link";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API).body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_422, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with JSON Body with valid link inside = 201 (created).
     */
    @Test
    public void onRequestWithCorrectLinkStatusIs201() {
        String longLink = "https://kyberorg.io"; // That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    /**
     * Request with JSON Body with valid link = 201 and reply with ident inside.
     */
    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithIdent() {
        String longLink = "https://kyberorg.io"; // That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_201, result.getStatus());

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

    /**
     * Request with JSON Body with link without protocol = 201.
     */
    @Test
    public void onRequestWithLinkWithoutProtocolStatusIs201() {
        String linkWithoutProtocol = "github.com/kyberorg/yalsee/issues/50";
        String correctJson = StoreRequestJson.create().withLink(linkWithoutProtocol).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_201, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    /**
     * Tests that API gives 403 status on request with banned link.
     */
    @Test
    public void onRequestWithBannedLinkStatusIs403() {
        String bannedUrl = "http://ct26737.tmweb.ru/compte/";
        String correctJson = StoreRequestJson.create().withLink(bannedUrl).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_403, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }
}
