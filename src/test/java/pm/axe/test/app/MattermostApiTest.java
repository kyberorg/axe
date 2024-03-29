package pm.axe.test.app;

import kong.unirest.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.api.mm.MattermostRestController;
import pm.axe.json.MattermostResponse;
import pm.axe.test.utils.TestUtils;
import pm.axe.test.utils.mock.MattermostMock;
import pm.axe.utils.AppUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Usage tests for {@link MattermostRestController}.
 *
 * @since 2.3
 */
@SuppressWarnings({"unchecked", "RawTypeCanBeGeneric", "rawtypes"})
public class MattermostApiTest extends UnirestTest {
    public static final String TAG = "[" + MattermostApiTest.class.getSimpleName() + "]";

    private static final int CHANNEL_TEAM_USER_LENGTH = 6;
    private static final int TOKEN_LENGTH = 15;

    /**
     * Request to store correct link = 200.
     */
    @Test
    public void toNormalPayloadShouldReplyWith200AndCorrectJsonAndWithContentType() {

        MattermostMock matterPayload = getMock("https%3A%2F%2Faxe.pm");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);
    }

    /**
     * If Payload JSON has extra space, app should drop it and store correct link.
     */
    @Test
    public void toPayloadWithTrailingSpaceShouldReplyCorrectJson() {
        MattermostMock matterPayload = getMock("https%3A%2F%2Faxe.pm+"); // Space encodes as +

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);
    }

    /**
     * If request has username, reply should have it as well.
     */
    @Test
    public void toPayloadWithUsernameShouldReplyWithCorrectJsonAndTextContainsThisUser() {
        String uzer = "uzer";

        MattermostMock matterPayload = getMock("https%3A%2F%2Faxe.pm");
        matterPayload.withUsername(uzer);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);
        assertTrue(mmText.contains(Axe.C.AT + uzer), "Mattermost test should have username in message");
    }

    /**
     * Empty body in request = 400.
     */
    @Test
    public void forPayloadWithoutBodyShouldReplyWith400() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API)
                        .body("")
                        .header(Axe.Headers.CONTENT_TYPE, MimeTypes.JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Something strange in request = 400.
     */
    @Test
    public void forPayloadWithStrangeBodyShouldReplyWithUsage() {
        String strangeBody = "a=haba$b=more";

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API)
                        .body(strangeBody)
                        .header(Axe.Headers.CONTENT_TYPE, MimeTypes.JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);

        assertUsage(mmText);
    }

    /**
     * Request body without link = Reply with 'usage' message.
     */
    @Test
    public void forPayloadWhereTextIsNotLinkShouldReplyWithCorrectMMJsonAndErrorMessageWithUsage() {
        MattermostMock matterPayload = getMock("ThisIsStringWithoutUrl");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);

        assertUsage(mmText);
    }

    /**
     * Request with single space only = Reply with 'usage' message.
     */
    @Test
    public void whenArgIsOnlySingleSpaceShouldShowUsage() {
        MattermostMock matterPayload = getMock("+");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    /**
     * Request with spaces only = Reply with 'usage' message.
     */
    @Test
    public void whenArgContainsOnlySpacesShouldShowUsage() {
        MattermostMock matterPayload = getMock("+++");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API)
                        .body(matterPayload.toString())
                        .header(Axe.Headers.CONTENT_TYPE, MimeTypes.JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    /**
     * Request has two parts and both are not links = Reply with 'usage' message.
     */
    @Test
    public void whenTextContainTwoArgsAndFirstIsNotLinkShouldShowUsage() {
        MattermostMock matterPayload = getMock("First+Second");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeTypes.JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    /**
     * Request has link and desired description = Reply with short link and given description.
     */
    @Test
    public void whenTextIsURLAndDescriptionShouldReturnShortLinkAndDescription() {
        String description = "TestDescription";
        MattermostMock matterPayload = getMock("https%3A%2F%2Faxe.pm+" + description);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(
                isResultMattermostReplyJson(result),
                "Reply should valid " + MattermostResponse.class.getSimpleName() + " object");
        String mmText = getMMText(result);
        assertTrue(mmText.contains(description), "Text must contain description, if it is present");
    }

    /**
     * Request has link and multi-word description = Reply with short link and given multi-word description.
     */
    @Test
    public void whenTextHasMultiWordDescriptionShouldReturnShortLinkAndMultiWordDescription() {
        String description = "Multi Test Description";
        String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8);
        MattermostMock matterPayload = getMock("https%3A%2F%2Faxe.pm+" + encodedDescription);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(
                isResultMattermostReplyJson(result),
                "Reply should valid " + MattermostResponse.class.getSimpleName() + " object");
        String mmText = getMMText(result);
        assertTrue(mmText.contains(description), "Text must contain description, if it is present");
    }

    private MattermostMock getMock(final String text) {

        return MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(CHANNEL_TEAM_USER_LENGTH))
                .withChannelName("channelName")
                .withCommand("axe")
                .withTeamDomain("myTeam")
                .withTeamId(RandomStringUtils.randomAlphanumeric(CHANNEL_TEAM_USER_LENGTH))
                .withText(text)
                .withToken(RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH))
                .withUserId(RandomStringUtils.randomAlphanumeric(CHANNEL_TEAM_USER_LENGTH))
                .withUsername("uzer");
    }

    private boolean isResultMattermostReplyJson(final HttpResponse<String> result) {
        String body = result.getBody();
        try {
            MattermostResponse mmJson = AppUtils.GSON.fromJson(body, MattermostResponse.class);
            return mmJson != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMMText(final HttpResponse<String> result) {
        String body = result.getBody();
        MattermostResponse mmJson = AppUtils.GSON.fromJson(body, MattermostResponse.class);
        return mmJson.getText();
    }

    private void assertUsage(final String mmText) {
        assertTrue(mmText.contains(Axe.Emoji.INFO), "Text must contain info emoji");
        assertTrue(mmText.contains("Usage"), "Text must contain word 'Usage'");
    }
}
