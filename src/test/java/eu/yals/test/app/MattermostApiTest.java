package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.json.MattermostResponseJson;
import eu.yals.test.utils.mock.MattermostMock;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.net.URLEncoder;

import static eu.yals.test.TestUtils.assertContentType;
import static org.junit.Assert.*;

/**
 * Usage tests for {@link MattermostRestController}
 *
 * @since 2.3
 */
@SuppressWarnings({"unchecked", "RawTypeCanBeGeneric", "rawtypes"})
public class MattermostApiTest extends UnirestTest {
  public static final String TAG = "[" + MattermostApiTest.class.getSimpleName() + "]";

  @Test
  public void toNormalPayloadShouldReplyWith200AndCorrectJsonAndWithContentType() {

    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("https%3A%2F%2Fyals.eu")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);
  }

  @Test
  public void toPayloadWithTrailingSpaceShouldReplyCorrectJson() {
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("https%3A%2F%2Fyals.eu+") // Space encodes as +
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);
  }

  @Test
  public void toPayloadWithUsernameShouldReplyWithCorrectJsonAndTextContainsThisUser() {
    String uzer = "uzer";

    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("https%3A%2F%2Fyals.eu")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername(uzer);

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);
    assertTrue("Mattermost test should have username in message", mmText.contains(App.AT + uzer));
  }

  @Test
  public void forReplyWithoutBodyShouldReplyWith400() {
    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API)
            .body("")
            .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(400, result.getStatus());
  }

  @Test
  public void forReplyWithStrangeBodyShouldReplyWithUsage() {
    String strangeBody = "a=haba$b=more";

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API)
            .body(strangeBody)
            .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());

    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);

    assertUsage(mmText);
  }

  @Test
  public void forReplyWhereTextIsNotLinkShouldReplyWithCorrectMMJsonAndErrorMessageWithUsage() {
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("ThisIsStringWithoutUrl")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());

    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);

    assertUsage(mmText);
  }

  @Test
  public void whenArgIsOnlySingleSpaceShouldShowUsage() {
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("+")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());

    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);
    assertUsage(mmText);
  }

  @Test
  public void whenArgContainsOnlySpacesShouldShowUsage() {
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("+++")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API)
            .body(matterPayload.toString())
            .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());

    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);
    assertUsage(mmText);
  }

  @Test
  public void whenTextContainTwoArgsAndFirstIsNotLinkShouldShowUsage() {
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("First+Second")
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(isResultMattermostReplyJson(result));
    assertContentType(MimeType.APPLICATION_JSON, result);

    String mmText = getMMText(result);
    assertUsage(mmText);
  }

  @Test
  public void whenTextIsURLAndTextShouldReturnShortLinkAndDescription() {
    String description = "TestDescription";
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("https%3A%2F%2Fyals.eu+" + description)
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(
        "Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object",
        isResultMattermostReplyJson(result));
    String mmText = getMMText(result);
    assertTrue("Text must contain description, if it is present", mmText.contains(description));
  }

  @Test
  public void whenTextHasMultiWordDescriptionShouldReturnShortLinkAndMultiWordDescription()
      throws Exception {
    String description = "Multi Test Description";
    String encodedDescription = URLEncoder.encode(description, "UTF-8");
    MattermostMock matterPayload =
        MattermostMock.create()
            .withChannelId(RandomStringUtils.randomAlphanumeric(6))
            .withChannelName("channelName")
            .withCommand("yals")
            .withTeamDomain("myTeam")
            .withTeamId(RandomStringUtils.randomAlphanumeric(6))
            .withText("https%3A%2F%2Fyals.eu+" + encodedDescription)
            .withToken(RandomStringUtils.randomAlphanumeric(15))
            .withUserId(RandomStringUtils.randomAlphanumeric(6))
            .withUsername("uzer");

    HttpRequest request =
        Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
    HttpResponse<String> result = request.asString();

    logRequestAndResponse(request, result, TAG);

    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertTrue(
        "Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object",
        isResultMattermostReplyJson(result));
    String mmText = getMMText(result);
    assertTrue("Text must contain description, if it is present", mmText.contains(description));
  }

  private boolean isResultMattermostReplyJson(HttpResponse<String> result) {
    String body = result.getBody();
    try {
      MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
      return mmJson != null;
    } catch (Exception e) {
      return false;
    }
  }

  private String getMMText(HttpResponse<String> result) {
    String body = result.getBody();
    MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
    return mmJson.getText();
  }

  private void assertUsage(String mmText) {
    assertTrue("Text must contain info emoji", mmText.contains(App.Emoji.INFO));
    assertTrue("Text must contain word 'Usage'", mmText.contains("Usage"));
  }
}
