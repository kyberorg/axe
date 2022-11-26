package pm.axe.test.app;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pm.axe.Endpoint;
import pm.axe.constants.Header;
import pm.axe.constants.HttpCode;
import pm.axe.test.TestApp;
import pm.axe.test.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Delete Link API.
 *
 * @since 3.0.4
 */
public class DeleteLinkApiTest extends UnirestTest {
    private static final String TAG = "[" + DeleteLinkApiTest.class.getSimpleName() + "]";

    /**
     * Tests that on request without any token - 401 Unauthorized given in response.
     */
    @Test
    public void onRequestWithNoTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNAUTHORIZED, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    /**
     * Tests that on request with not valid token - 401 Unauthorized given in response.
     */
    @Test
    public void onRequestWithWrongTokenStatusIs401() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        request.header(Header.X_AXE_TOKEN, "wrongTokenVoid");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNAUTHORIZED, result.getStatus());

        assertTrue(verifyLinkIsStored(ident), "Link should not be deleted");
    }

    /**
     * Tests that on request with valid token - link actually deleted and 204 (no content) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_MASTER_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenStatusIs204AndLinkDeleted() {
        String ident = store("https://kyberorg.io");
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);

        request.header(Header.X_AXE_TOKEN, TestUtils.getDeleteToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.NO_CONTENT, result.getStatus());

        assertFalse(verifyLinkIsStored(ident), "Link should be deleted");
    }

    /**
     * Tests that on request with valid token, but non-existing ident 404 (Not Found) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_MASTER_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void onRequestWithCorrectTokenButWrongIdentStatusIs404() {
        String ident = "rndStr";
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);

        request.header(Header.X_AXE_TOKEN, TestUtils.getDeleteToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.NOT_FOUND, result.getStatus());
    }

    /**
     * Tests that on second request with same ident 404 (Not Found) given in response.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_MASTER_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Deletion Token Provided")
    public void createDeleteVerifyThatSecondDeleteWillGive404() {
        String ident = store("https://kyberorg.io");

        //first request
        HttpRequestWithBody request = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);

        request.header(Header.X_AXE_TOKEN, TestUtils.getDeleteToken());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.NO_CONTENT, result.getStatus());

        //second request
        HttpRequestWithBody request2 = Unirest.delete(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        request2.header(Header.X_AXE_TOKEN, TestUtils.getDeleteToken());
        HttpResponse<String> result2 = request2.asString();

        logRequestAndResponse(request2, result2, TAG);

        assertNotNull(result2);
        assertEquals(HttpCode.NOT_FOUND, result2.getStatus());
    }
}
