package pm.axe.test.app;

import pm.axe.Endpoint;
import pm.axe.constants.App;
import pm.axe.constants.HttpCode;
import pm.axe.constants.MimeType;
import pm.axe.json.PostLinkRequest;
import pm.axe.json.PostLinkResponse;
import pm.axe.test.AxeTest;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import pm.axe.utils.AppUtils;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Field;

import static pm.axe.constants.Header.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests, where we run application same ways in {@link SelenideTest} and test.
 * by doing requests using {@link kong.unirest.Unirest}.
 *
 * @since 2.5.1
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public abstract class UnirestTest extends AxeTest {
    protected static final String TEST_URL = TestUtils.getTestUrl();

    protected static final String LINK_NOT_FOUND_STATUS = "LINK_NOT_FOUND";

    private static String TAG = "[" + UnirestTest.class.getSimpleName() + "]";

    /**
     * Logs both request and response.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param tag      string with custom tag
     */
    public void logRequestAndResponse(final HttpRequest request, final HttpResponse response, final String tag) {
        TAG = tag;
        logRequest(request);
        logResponse(response);
    }

    /**
     * Stores long link.
     *
     * @param longLink string with long URL to store
     * @return string with ident
     */
    protected String store(final String longLink) {
        String requestJson = PostLinkRequest.create().withLink(longLink).toString();

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                .body(requestJson);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertNotNull(result);
        assertEquals(HttpCode.CREATED, result.getStatus());

        String responseBody = result.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        PostLinkResponse replyJson;
        replyJson = AppUtils.GSON.fromJson(responseBody, PostLinkResponse.class);
        return replyJson.getIdent();
    }

    /**
     * Finds link by its ident.
     *
     * @param ident non-empty string with ident to search
     * @return string with long URL or {@link #LINK_NOT_FOUND_STATUS} if not found
     */
    protected String getStoredLink(final String ident) {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.LINKS_API + "/" + ident);
        HttpResponse<JsonNode> result = request.asJson();
        logRequestAndResponse(request, result, TAG);

        if (result.getStatus() == HttpCode.OK) {
            return result.getBody().getObject().getString("link");
        } else if (result.getStatus() == HttpCode.NOT_FOUND) {
            return LINK_NOT_FOUND_STATUS;
        } else {
            throw new RuntimeException("Error occurred while retrieving link. Please see logs above");
        }
    }

    /**
     * Check if link with given ident stored within application.
     *
     * @param ident string with ident to search
     * @return true - if link found, false - else.
     */
    protected boolean verifyLinkIsStored(final String ident) {
        String storedLink = getStoredLink(ident);
        boolean responseIsNotEmpty = StringUtils.isNotBlank(storedLink);
        boolean responseIsNotLinkNotFoundStatus = !storedLink.equals(LINK_NOT_FOUND_STATUS);

        return responseIsNotEmpty && responseIsNotLinkNotFoundStatus;
    }

    /**
     * Alias for {@link #verifyLinkIsStored(String)}.
     *
     * @param ident string with ident to search
     * @return true - if link exists, false - else.
     */
    protected boolean isIdentAlreadyExists(final String ident) {
        return verifyLinkIsStored(ident);
    }

    private void logRequest(final HttpRequest request) {
        StringBuilder reqLog = new StringBuilder("Request: ").append(App.NEW_LINE);
        reqLog.append(String.format("Request URL: %s", request.getUrl())).append(App.NEW_LINE);
        reqLog
                .append(String.format("Request method: %s", request.getHttpMethod().name()))
                .append(App.NEW_LINE);
        if (request.getBody().isPresent()) {
            try {
                reqLog
                        .append(String.format("Request body: %s", getRequestBody(request)))
                        .append(App.NEW_LINE);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                reqLog
                        .append(String.format("Request body: %s", "<failed to retrieve>"))
                        .append(App.NEW_LINE);
            }
        }
        if (request.getHeaders().size() > 0) {
            reqLog.append("Request headers: ").append(App.NEW_LINE);
            for (kong.unirest.Header header : request.getHeaders().all()) {
                reqLog
                        .append(String.format("%s: %s", header.getName(), header.getValue()))
                        .append(App.NEW_LINE);
            }
        }
        log.info(String.format("%s %s", TAG, reqLog));
    }

    private void logResponse(final HttpResponse response) {
        StringBuilder respLog = new StringBuilder("Response: ").append(App.NEW_LINE);
        respLog
                .append(String.format("Response status code: %s", response.getStatus()))
                .append(App.NEW_LINE);
        respLog.append(String.format("Response body: %s", response.getBody())).append(App.NEW_LINE);
        if (response.getHeaders().size() > 0) {
            respLog.append("Response headers: ").append(App.NEW_LINE);
            for (kong.unirest.Header header : response.getHeaders().all()) {
                respLog
                        .append(String.format("%s: %s", header.getName(), header.getValue()))
                        .append(App.NEW_LINE);
            }
        }
        log.info(String.format("%s %s", TAG, respLog));
    }

    private Object getRequestBody(final HttpRequest request)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = request.getClass().getDeclaredField("body");
        f.setAccessible(true);
        Object bodyPart = f.get(request);
        if (bodyPart instanceof BodyPart) {
            return ((BodyPart) bodyPart).getValue();
        } else {
            throw new NoSuchFieldException("body is not instance of " + BodyPart.class.getSimpleName());
        }
    }
}
