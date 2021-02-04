package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.json.StoreRequestJson;
import io.kyberorg.yalsee.json.StoreResponseJson;
import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static io.kyberorg.yalsee.Endpoint.ForTests.SLASH_BASE;
import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for /{ident}.
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class SlashEndpointTest extends UnirestTest {
    public static final String TAG = "[" + SlashEndpointTest.class.getSimpleName() + "]";

    /**
     * Tests that stored URL redirects to original long URL.
     */
    @Test
    public void storeURLAndCheckIfRedirectToSameURL() {
        String url = "https://ci.yadev.eu";
        String ident = store(url);
        assertNotNull(ident);

        Unirest.config().reset().followRedirects(false);

        HttpRequest request = Unirest.get(TEST_URL + SLASH_BASE + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        Unirest.config().reset().followRedirects(true);

        assertEquals(STATUS_302, result.getStatus());
        assertTrue(result.getHeaders().containsKey(Header.LOCATION));
        String location = result.getHeaders().getFirst(Header.LOCATION);
        assertTrue(StringUtils.isNotBlank(location), "Got empty " + Header.LOCATION + " header");
    }

    /**
     * Request something that not exists = 404.
     */
    @Test
    public void requestWithIdentThatNotStoredGivesStatus404() {
        String ident = "habaHaba";

        HttpRequest request = Unirest.get(TEST_URL + SLASH_BASE + ident);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());
    }

    private String store(final String urlToStore) {
        String requestBody = StoreRequestJson.create().withLink(urlToStore).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(requestBody);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_201, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        assertNotNull(replyJson);
        return replyJson.getIdent();
    }
}
