package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.api.mm.MattermostRestController;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.json.MattermostResponse;
import io.kyberorg.yalsee.test.utils.TestUtils;
import io.kyberorg.yalsee.test.utils.mock.MattermostMock;
import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_200;
import static io.kyberorg.yalsee.constants.HttpCode.STATUS_400;
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

        MattermostMock matterPayload = getMock("https%3A%2F%2Fyals.eu");

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * If Payload JSON has extra space, app should drop it and store correct link.
     */
    @Test
    public void toPayloadWithTrailingSpaceShouldReplyCorrectJson() {
        MattermostMock matterPayload = getMock("https%3A%2F%2Fyals.eu+"); // Space encodes as +

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * If request has username, reply should have it as well.
     */
    @Test
    public void toPayloadWithUsernameShouldReplyWithCorrectJsonAndTextContainsThisUser() {
        String uzer = "uzer";

        MattermostMock matterPayload = getMock("https%3A%2F%2Fyals.eu");
        matterPayload.withUsername(uzer);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertTrue(mmText.contains(App.AT + uzer), "Mattermost test should have username in message");
    }

    /**
     * Empty body in request = 400.
     */
    @Test
    public void forPayloadWithoutBodyShouldReplyWith400() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API)
                        .body("")
                        .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
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
                        .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

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
        assertEquals(STATUS_200, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

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
        assertEquals(STATUS_200, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

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
                        .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

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
        assertEquals(STATUS_200, result.getStatus());
        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    /**
     * Request has link and desired description = Reply with short link and given description.
     */
    @Test
    public void whenTextIsURLAndDescriptionShouldReturnShortLinkAndDescription() {
        String description = "TestDescription";
        MattermostMock matterPayload = getMock("https%3A%2F%2Fyals.eu+" + description);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());
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
        MattermostMock matterPayload = getMock("https%3A%2F%2Fyals.eu+" + encodedDescription);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API).body(matterPayload.toString());
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_200, result.getStatus());
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
                .withCommand("yalsee")
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
        assertTrue(mmText.contains(App.Emoji.INFO), "Text must contain info emoji");
        assertTrue(mmText.contains("Usage"), "Text must contain word 'Usage'");
    }
}
