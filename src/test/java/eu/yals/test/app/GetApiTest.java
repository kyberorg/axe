package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.controllers.rest.GetRestController;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing {@link GetRestController}
 *
 * @since 1.0
 */
public class GetApiTest extends UnirestTest {

    @Test
    public void onRequestWithoutIdentStatusIs400() {
        HttpResponse<String> result = uniGet(Endpoint.LINK_API);
        assertNotNull(result);
        assertEquals(400, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithSpaceIdentStatusIs400() {
        HttpResponse<String> result = uniGet(Endpoint.LINK_API + " ");
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithSpecialCharIdentStatusIs400() {
        HttpResponse<String> result = uniGet(Endpoint.LINK_API + "%#");
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithNotExistingIdentStatusIs404() {
        HttpResponse<String> result = uniGet(Endpoint.LINK_API + "notStoredIdent");
        assertNotNull(result);
        assertEquals(404, result.getStatus());
        TestUtils.assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithExistingIdentStatusIs200() {
        String longLink = "http://virtadev.net"; //That very long, really
        String ident = store(longLink);

        HttpResponse<String> result = uniGet(Endpoint.LINK_API + ident);
        assertNotNull(result);
        assertEquals(200, result.getStatus());

        TestUtils.assertResultIsErrorJson(result);
    }

    private String store(String longLink) {

        String requestJson = StoreRequestJson.create().withLink(longLink).toString();

        HttpResponse<String> result = uniPost(Endpoint.STORE_API, requestJson);
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
