package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.api.links.GetLinkRestController;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.utils.UrlUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.kyberorg.yalsee.constants.HttpCode.*;
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
        assertEquals(STATUS_501, result.getStatus());

        TestUtils.assertResultIsYalsErrorJson(result);
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
        assertEquals(STATUS_501, result.getStatus());
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
                        + Endpoint.Api.LINKS_API + "/"
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
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/" + "notStoredIdent");
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
        String longLink = "https://kyberorg.io"; // That very long, really
        String ident = store(longLink);

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());

        TestUtils.assertResultIsJson(result);
    }


}
