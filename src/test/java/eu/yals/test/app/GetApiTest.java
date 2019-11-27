package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.controllers.rest.GetRestController;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.*;

/**
 * Testing {@link GetRestController}
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class GetApiTest extends UnirestTest {
    public static final String TAG = "[" + GetApiTest.class.getSimpleName() + "]";

    @Test
    public void onRequestWithoutIdentStatusIs400() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.LINK_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(400, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithSpaceIdentStatusIs400() {
        String url = TEST_URL + Endpoint.LINK_API + " ";
        HttpRequest request = Unirest.get(AppUtils.covertUnicodeToAscii(url));
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithSpecialCharIdentStatusIs400() throws UnsupportedEncodingException {
        String specChars = "%#";
        String url = TEST_URL + Endpoint.LINK_API + URLEncoder.encode(specChars, "UTF-8"); //because '%' should be encoded
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithNotExistingIdentStatusIs404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.LINK_API + "notStoredIdent");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(404, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithExistingIdentStatusIs200() {
        String longLink = "http://virtadev.net"; //That very long, really
        String ident = store(longLink);

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.LINK_API + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(200, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    private String store(String longLink) {
        String requestJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.STORE_API).body(requestJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(201, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson;
        replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        return replyJson.getIdent();
    }
}
