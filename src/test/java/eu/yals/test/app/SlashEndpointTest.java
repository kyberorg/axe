package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static eu.yals.Endpoint.ForTests.SLASH_BASE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for /{ident}
 *
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class SlashEndpointTest extends UnirestTest {
  public static final String TAG = "[" + SlashEndpointTest.class.getSimpleName() + "]";

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

    Assert.assertEquals(302, result.getStatus());
    Assert.assertTrue(result.getHeaders().containsKey(Header.LOCATION));
    String location = result.getHeaders().getFirst(Header.LOCATION);
    Assert.assertTrue("Got empty " + Header.LOCATION + " header", StringUtils.isNotBlank(location));
  }

  @Test
  public void requestWithIdentThatNotStoredGivesStatus404() {
    String ident = "habaHaba";

    HttpRequest request = Unirest.get(TEST_URL + SLASH_BASE + ident);
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    Assert.assertEquals(404, result.getStatus());
  }

  private String store(String urlToStore) {
    String requestBody = StoreRequestJson.create().withLink(urlToStore).toString();

    HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(requestBody);
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    Assert.assertEquals(201, result.getStatus());

    String responseBody = result.getBody();
    assertNotNull(responseBody);
    assertFalse(responseBody.trim().isEmpty());

    StoreResponseJson replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
    assertNotNull(replyJson);
    return replyJson.getIdent();
  }
}
