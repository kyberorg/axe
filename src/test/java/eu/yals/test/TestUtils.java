package eu.yals.test;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.ErrorJson;
import eu.yals.test.utils.Selenide;
import eu.yals.utils.AppUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Useful stuff for testing
 *
 * @since 2.0
 */
@SuppressWarnings("ConstantConditions") //false positive
public class TestUtils {

    public static void assertResultIsJson(MvcResult result) {
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertTrue(result.getResponse().containsHeader(Header.CONTENT_TYPE));
        assertFalse(result.getResponse().getHeader(Header.CONTENT_TYPE).isEmpty());
        assertTrue(result.getResponse().getHeader(Header.CONTENT_TYPE).contains(MimeType.APPLICATION_JSON));
    }

    public static void assertResultIsErrorJson(MvcResult result) throws Exception {
        assertTrue("Response is not valid " + ErrorJson.class.getSimpleName(), TestUtils.isValidErrorJson(result));
    }

    public static void assertContentNotEmpty(MvcResult result) throws UnsupportedEncodingException {
        assertContentNotEmpty("Content is empty", result);
    }

    public static void assertContentNotEmpty(HttpResponse<String> response) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotEquals("", response.getBody().trim());
    }

    public static void assertContentNotEmpty(String message, MvcResult result) throws UnsupportedEncodingException {
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getResponse().getContentAsString());
        assertNotEquals(message, "", result.getResponse().getContentAsString().trim());
    }

    public static void assertContentType(String mimeType, MvcResult result) {
        assertNotNull(mimeType);
        assertNotNull(result);
        assertNotNull(result.getResponse());

        String contentType = result.getResponse().getContentType();
        assertNotNull(contentType);
        String[] contentTypeParts = contentType.split(";");
        if (contentTypeParts.length > 1) {
            String onlyContentType = contentTypeParts[0];
            assertEquals(mimeType, onlyContentType);
        } else {
            assertEquals(mimeType, result.getResponse().getContentType());
        }
    }

    public static void assertContentType(String mimeType, HttpResponse<String> response) {
        assertNotNull(mimeType);
        assertNotNull(response);

        Headers headers = response.getHeaders();
        assertNotNull(headers);
        String contentType = headers.getFirst(Header.CONTENT_TYPE);
        assertNotNull(contentType);
        String[] contentTypeParts = contentType.split(";");
        if (contentTypeParts.length > 1) {
            String onlyContentType = contentTypeParts[0];
            assertEquals(mimeType, onlyContentType);
        } else {
            assertEquals(mimeType, contentType);
        }
    }

    public static String whichBrowser() {
        return System.getProperty(TestApp.Selenide.BROWSER, Selenide.Browser.HTMLUNIT);
    }

    public static String getTestUrl() {
        final int serverPort = Integer.parseInt(System.getProperty(App.Properties.SERVER_PORT, "8080"));
        final String localUrl;
        String runMode = System.getProperty(TestApp.Properties.RUN_MODE, TestApp.RunMode.LOCAL.name());

        if (runMode.equals(TestApp.RunMode.CONTAINER.name())) {
            localUrl = String.format("http://host.testcontainers.internal:%d", serverPort);
        } else {
            localUrl = String.format("http://localhost:%d", serverPort);
        }

        return System.getProperty(TestApp.Properties.TEST_URL, localUrl);
    }

    public static boolean isLocalRun() {
        String testUrl = getTestUrl();
        String dockerHost = "host.testcontainers.internal";
        String localhost = "localhost";

        return (testUrl.contains(dockerHost) || testUrl.contains(localhost));
    }

    private static boolean isValidErrorJson(MvcResult mvcResult) throws Exception {
        String body = mvcResult.getResponse().getContentAsString();
        try {
            ErrorJson errorJson = AppUtils.GSON.fromJson(body, ErrorJson.class);
            return errorJson != null;
        } catch (Exception e) {
            return false;
        }
    }
}
