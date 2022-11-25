package pm.axe.test.app;

import pm.axe.Endpoint;
import pm.axe.constants.HttpCode;
import pm.axe.constants.MimeType;
import pm.axe.json.EmptyJson;
import pm.axe.json.PostLinkRequest;
import pm.axe.json.PostLinkResponse;
import pm.axe.test.TestApp;
import pm.axe.test.utils.TestUtils;
import pm.axe.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junitpioneer.jupiter.Issue;

import static pm.axe.constants.Header.CONTENT_TYPE;
import static pm.axe.constants.Header.X_AXE_TOKEN;
import static pm.axe.core.IdentGenerator.IDENT_DEFAULT_LENGTH;
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
        assertEquals(HttpCode.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request without Content-Type header = 415.
     */
    @Test
    public void onRequestWithoutContentTypeHeaderStatusIs415() {
        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API).body("");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNSUPPORTED_MEDIA_TYPE, result.getStatus());
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
        assertEquals(HttpCode.BAD_REQUEST, result.getStatus());
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
        assertEquals(HttpCode.BAD_REQUEST, result.getStatus());

        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with JSON Body without link inside = 422.
     */
    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs422() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                        .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                        .body(EmptyJson.create().toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNPROCESSABLE_ENTRY, result.getStatus());

        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with JSON Body with empty link inside = 422.
     */
    @Test
    public void onRequestWithEmptyLinkStatusIs422() {
        String longLink = "";
        String correctJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNPROCESSABLE_ENTRY, result.getStatus());

        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with JSON Body with non-valid link inside = 422.
     */
    @Test
    public void onRequestWithNotALinkStatusIs422() {
        String longLink = "not a Link";
        String correctJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.UNPROCESSABLE_ENTRY, result.getStatus());

        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with JSON Body with valid link inside = 201 (created).
     */
    @Test
    public void onRequestWithCorrectLinkStatusIs201() {
        String longLink = "https://kyberorg.io"; // That very long, really
        String correctJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    /**
     * Request with JSON Body with valid link = 201 and reply with ident inside.
     */
    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithIdent() {
        String longLink = "https://kyberorg.io"; // That very long, really
        String correctJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        PostLinkResponse replyJson;
        try {
            replyJson = AppUtils.GSON.fromJson(responseBody, PostLinkResponse.class);
        } catch (Exception e) {
            fail("Could not parse reply JSON");
            return;
        }

        assertNotNull(replyJson);
        assertNotNull(replyJson.getIdent());
    }

    /**
     * Request with JSON Body with valid link = 201 and reply with short FQDN link inside.
     */
    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithShortFQDNLink() {
        String longLink = "https://kyberorg.io"; // That very long, really
        String correctJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        PostLinkResponse replyJson;
        try {
            replyJson = AppUtils.GSON.fromJson(responseBody, PostLinkResponse.class);
        } catch (Exception e) {
            fail("Could not parse reply JSON");
            return;
        }

        assertNotNull(replyJson);
        assertNotNull(replyJson.getLink());

        assertTrue(replyJson.getLink().contains(TestUtils.getTestedEnv().getShortUrl()),
                "Link in JSON should contain short URL");
    }

    /**
     * Request with JSON Body with link without protocol = 201.
     */
    @Test
    public void onRequestWithLinkWithoutProtocolStatusIs201() {
        String linkWithoutProtocol = "github.com/kyberorg/axe/issues/50";
        String correctJson = PostLinkRequest.create().withLink(linkWithoutProtocol).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    /**
     * Tests that API gives 403 status on request with banned link.
     */
    @Test
    public void onRequestWithBannedLinkStatusIs403() {
        String bannedUrl = "http://ct26737.tmweb.ru/compte/";
        String correctJson = PostLinkRequest.create().withLink(bannedUrl).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(correctJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.FORBIDDEN, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }

    /**
     * Tests that API can create Link if custom Ident contains numbers.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_MASTER_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Token Provided")
    public void apiCanCreateLinkIfCustomIdentContainsNumber() {
        String longUrl = "https://github.com/kyberorg/axe/issues/537";
        String customIdentWithNumbers;
        do {
            customIdentWithNumbers = RandomStringUtils.randomAlphanumeric(IDENT_DEFAULT_LENGTH);
        } while (isIdentAlreadyExists(customIdentWithNumbers));

        PostLinkRequest req = PostLinkRequest.create().withLink(longUrl);
        req.setIdent(customIdentWithNumbers);
        String requestJson = req.toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .header(X_AXE_TOKEN, TestUtils.getDeleteToken())
                .body(requestJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());
    }

    /**
     * Tests that if desired ident conflicts with existing routes - status 409 is send.
     */
    @Test
    @EnabledIfSystemProperty(named = TestApp.Properties.TEST_MASTER_TOKEN, matches = ".*.*",
            disabledReason = "Only works when Token Provided")
    @Issue("https://github.com/kyberorg/axe/issues/633")
    public void onConflictingIdentStatusIs409() {
        String longUrl = "https://github.com/kyberorg/axe/issues/633";
        String customIdent = Endpoint.UI.APP_INFO_PAGE;

        PostLinkRequest req = PostLinkRequest.create().withLink(longUrl);
        req.setIdent(customIdent);
        String requestJson = req.toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .header(X_AXE_TOKEN, TestUtils.getDeleteToken())
                .body(requestJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CONFLICT, result.getStatus());
    }

}
