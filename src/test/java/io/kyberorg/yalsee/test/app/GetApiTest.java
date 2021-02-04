package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.controllers.rest.GetRestController;
import io.kyberorg.yalsee.json.StoreRequestJson;
import io.kyberorg.yalsee.json.StoreResponseJson;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing {@link GetRestController}.
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GetApiTest extends UnirestTest {
    public static final String TAG = "[" + GetApiTest.class.getSimpleName() + "]";

    /**
     * Request without ident = 400.
     */
    @Test
    public void onRequestWithoutIdentStatusIs400() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.LINK_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with space ident = 400.
     */
    @Test
    public void onRequestWithSpaceIdentStatusIs400() {
        String url = TEST_URL + Endpoint.ForTests.LINK_API + " ";
        HttpRequest request = Unirest.get(AppUtils.covertUnicodeToAscii(url));
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request with non-alphanumeric chars = 400.
     */
    @Test
    public void onRequestWithSpecialCharIdentStatusIs400() {
        String specChars = "%#";
        String url =
                TEST_URL
                        + Endpoint.ForTests.LINK_API
                        + URLEncoder.encode(specChars, StandardCharsets.UTF_8); // because '%' should be encoded
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request something that not exists = 404.
     */
    @Test
    public void onRequestWithNotExistingIdentStatusIs404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.LINK_API + "notStoredIdent");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_404, result.getStatus());
        TestUtils.assertResultIsYalsErrorJson(result);
    }

    /**
     * Request something that exists = 200.
     */
    @Test
    public void onRequestWithExistingIdentStatusIs200() {
        String longLink = "http://virtadev.net"; // That very long, really
        String ident = store(longLink);

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.LINK_API + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    private String store(final String longLink) {
        String requestJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(requestJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_201, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson;
        replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        return replyJson.getIdent();
    }
}
