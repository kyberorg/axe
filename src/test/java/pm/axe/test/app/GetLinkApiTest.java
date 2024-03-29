package pm.axe.test.app;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import pm.axe.Endpoint;
import pm.axe.api.links.GetLinkRestController;
import pm.axe.test.utils.TestUtils;
import pm.axe.utils.UrlUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing {@link GetLinkRestController}.
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GetLinkApiTest extends UnirestTest {
    public static final String TAG = "[" + GetLinkApiTest.class.getSimpleName() + "]";

    /**
     * Request without ident = 501.
     */
    @Test
    public void onRequestWithoutIdentStatusIs501() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, result.getStatus());

        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with space ident = 501.
     */
    @Test
    public void onRequestWithSpaceIdentStatusIs501() {
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + " ";
        HttpRequest request = Unirest.get(UrlUtils.covertUnicodeUrlToAscii(url));
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with non-alphanumeric chars = 500. As Spring HTTP Firewall will reject this URL
     */
    @Test
    public void onRequestWithSpecialCharIdent_StatusIs500_and_SpringFirewallRejectedError() {
        String specChars = "%#";
        String url =
                TEST_URL
                        + Endpoint.Api.LINKS_API + "/"
                        + URLEncoder.encode(specChars, StandardCharsets.UTF_8); // because '%' should be encoded
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
    }

    /**
     * Request with dot inside = 404.
     */
    @Test
    public void onRequestWithDotInsideIdentStatusIs404() {
        String ident = "a.b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with dot at first place = 400.
     */
    @Test
    public void onRequestWithDotAsFirstCharIdentStatusIs400() {
        String ident = ".b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with dot at last place = 400.
     */
    @Test
    public void onRequestWithDotAsLastCharIdentStatusIs400() {
        String ident = "b.";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with hyphen inside = 404.
     */
    @Test
    public void onRequestWithHyphenInsideIdentStatusIs200() {
        String ident = "a-b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with dot at hyphen place = 400.
     */
    @Test
    public void onRequestWithHyphenAsFirstCharIdentStatusIs400() {
        String ident = "-b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with hyphen at last place = 400.
     */
    @Test
    public void onRequestWithHyphenAsLastCharIdentStatusIs400() {
        String ident = "b.";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with underscore inside = 404.
     */
    @Test
    public void onRequestWithUnderscoreInsideIdentStatusIs404() {
        String ident = "a_b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with underscore at first place = 400.
     */
    @Test
    public void onRequestWithUnderscoreAsFirstCharIdentStatusIs400() {
        String ident = "_b";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with underscore at last place = 400.
     */
    @Test
    public void onRequestWithUnderscoreAsLastCharIdentStatusIs400() {
        String ident = "b.";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with allowed chars inside = 404.
     */
    @Test
    public void onRequestWithAllowedCharsInsideIdentStatusIs404() {
        String ident = "a_and-b.are.allowed";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request with allowed and not allowed together in ident = 400.
     */
    @Test
    public void onRequestWithAllowedAndNotAllowedCharsTogetherInIdentStatusIs400() {
        String ident = "b-#";
        String url = TEST_URL + Endpoint.Api.LINKS_API + "/" + ident;
        HttpRequest request = Unirest.get(url);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request something that not exists = 404.
     */
    @Test
    public void onRequestWithNotExistingIdentStatusIs404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/" + "notStoredIdent");
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        TestUtils.assertResultIsAxeErrorJson(result);
    }

    /**
     * Request something that exists = 200.
     */
    @Test
    public void onRequestWithExistingIdentStatusIs200() {
        String longLink = "https://kyberorg.io"; // That is very long, really
        String ident = store(longLink);

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }


}
