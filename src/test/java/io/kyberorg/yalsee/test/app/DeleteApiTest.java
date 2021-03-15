package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.test.TestApp;
import io.kyberorg.yalsee.test.TestUtils;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Delete Link API.
 *
 * @since 3.0.4
 */
public class DeleteApiTest extends UnirestTest {
    private static final String TAG = "[" + DeleteApiTest.class.getSimpleName() + "]";

    /**
     * Tests that request without ident always returns status 405 (Method not implemented).
     */
    @Test
    public void onRequestWithoutIdentStatusIs405() {
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_405, result.getStatus());
    }

    /**
     * Tests that on request without any token - 401 Unauthorized given in response.
     */
    @Test
    public void onRequestWithNoTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_401, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    /**
     * Tests that on request with not valid token - 401 Unauthorized given in response.
     */
    @Test
    public void onRequestWithWrongTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);
        request.header(Header.X_YALSEE_TOKEN, "wrongTokenVoid");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_401, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    /**
     * Tests that on request with valid token - link actually deleted and 204 (no content) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenStatusIs204AndLinkDeleted() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_204, result.getStatus());

        assertFalse(verifyLinkIsStored(ident), "Link should be deleted");
    }

    /**
     * Tests that on request with valid token, but non existing ident 404 (Not Found) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenButWrongIdentStatusIs404() {
        String ident = "rndStr";
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_404, result.getStatus());
    }

    /**
     * Tests that on second request with same ident 404 (Not Found) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void createDeleteVerifyThatSecondDeleteWillGive404() {
        String ident = store("https://kyberorg.io");

        //first request
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_204, result.getStatus());

        //second request
        HttpRequestWithBody request2 = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API + "/" + ident);
        request2.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result2 = request2.asString();

        logRequestAndResponse(request2, result2, TAG);

        assertNotNull(result2);
        assertEquals(STATUS_404, result2.getStatus());
    }

}
