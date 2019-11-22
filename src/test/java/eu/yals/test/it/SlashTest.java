package eu.yals.test.it;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link Endpoint#SLASH}
 *
 * @since 1.0
 */

@Slf4j
public class SlashTest {

    @Test
    public void storeURLAndCheckIfRedirectToSameURL() {
        String url = "https://ci.yadev.eu";
        String ident = store(url);
        assertNotNull(ident);

        HttpResponse<String> response;

        Unirest.config().reset().followRedirects(false);
        response = TestUtils.unirestGet(Endpoint.SLASH_BASE + ident);
        Unirest.config().reset().followRedirects(true);

        log.debug("Response: {}", response);
        if (response == null) return;

        Assert.assertEquals(302, response.getStatus());
        Assert.assertTrue(response.getHeaders().containsKey(Header.LOCATION));
        String location = response.getHeaders().getFirst(Header.LOCATION);
        Assert.assertTrue("Got empty " + Header.LOCATION + " header",
                StringUtils.isNotBlank(location));
    }

    @Test
    public void requestWithIdentThatNotStoredGivesStatus404() {
        String ident = "habaHaba";

        HttpResponse<String> response = TestUtils.unirestGet(Endpoint.SLASH_BASE + ident);
        log.debug("Response: {}", response);
        if (response == null) return;
        Assert.assertEquals(404, response.getStatus());
    }

    private String store(String urlToStore) {
        String request = StoreRequestJson.create().withLink(urlToStore).toString();

        HttpResponse<String> response = TestUtils.unirestPost(Endpoint.STORE_API, request);
        log.debug("Response: {}", response);
        if (response == null) throw new NullPointerException("Store Requested Failed: got nothing in return");
        Assert.assertEquals(201, response.getStatus());

        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        assertNotNull(replyJson);
        return replyJson.getIdent();
    }
}
