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
 * Tests for Delete Link API
 *
 * @since 3.0.4
 */
public class DeleteApiTest extends UnirestTest {
    private static final String TAG = "[" + DeleteApiTest.class.getSimpleName() + "]";

    @Test
    public void onRequestWithoutIdentStatusIs405() {
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_405, result.getStatus());
    }

    @Test
    public void onRequestWithNoTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_401, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    @Test
    public void onRequestWithWrongTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);
        request.header(Header.X_YALSEE_TOKEN, "wrongTokenVoid");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_401, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenStatusIs204AndLinkDeleted() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_204, result.getStatus());

        assertFalse(verifyLinkIsStored(ident), "Link should be deleted");
    }

    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenButWrongIdentStatusIs404() {
        String ident = "rnd123";
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_404, result.getStatus());
    }

    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_DELETE_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void createDeleteVerifyThatSecondDeleteWillGive404() {
        String ident = store("https://kyberorg.io");

        //first request
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);

        request.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_204, result.getStatus());

        //second request
        HttpRequestWithBody request2 = Unirest.delete(TEST_URL + Endpoint.Api.DELETE_LINK_API+"/" + ident);
        request2.header(Header.X_YALSEE_TOKEN, TestUtils.getDeletionToken());
        HttpResponse<String> result2 = request2.asString();

        logRequestAndResponse(request2, result2, TAG);

        assertNotNull(result2);
        assertEquals(STATUS_404, result2.getStatus());
    }

}
