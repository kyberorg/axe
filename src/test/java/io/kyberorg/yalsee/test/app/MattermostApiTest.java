package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.controllers.rest.MattermostRestController;
import io.kyberorg.yalsee.json.MattermostResponseJson;
import io.kyberorg.yalsee.test.TestUtils;
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

    @Test
    public void forReplyWithoutBodyShouldReplyWith400() {
        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.MM_API)
                        .body("")
                        .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(STATUS_400, result.getStatus());
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
        assertEquals(STATUS_200, result.getStatus());

        assertTrue(isResultMattermostReplyJson(result));
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);

        assertUsage(mmText);
    }

    @Test
    public void forReplyWhereTextIsNotLinkShouldReplyWithCorrectMMJsonAndErrorMessageWithUsage() {
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
                "Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object");
        String mmText = getMMText(result);
        assertTrue(mmText.contains(description), "Text must contain description, if it is present");
    }

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
                "Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object");
        String mmText = getMMText(result);
        assertTrue(mmText.contains(description), "Text must contain description, if it is present");
    }

    private MattermostMock getMock(final String text) {

        return MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6))
                .withChannelName("channelName")
                .withCommand("yalsee")
                .withTeamDomain("myTeam")
                .withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText(text)
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6))
                .withUsername("uzer");
    }

    private boolean isResultMattermostReplyJson(final HttpResponse<String> result) {
        String body = result.getBody();
        try {
            MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
            return mmJson != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMMText(final HttpResponse<String> result) {
        String body = result.getBody();
        MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
        return mmJson.getText();
    }

    private void assertUsage(final String mmText) {
        assertTrue(mmText.contains(App.Emoji.INFO), "Text must contain info emoji");
        assertTrue(mmText.contains("Usage"), "Text must contain word 'Usage'");
    }
}
