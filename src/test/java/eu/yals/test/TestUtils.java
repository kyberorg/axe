package eu.yals.test;

import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.ErrorJson;
import eu.yals.test.utils.Selenide;
import eu.yals.utils.AppUtils;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Useful stuff for testing
 *
 * @since 2.0
 */
public class TestUtils {

    public static void assertResultIsJson(HttpResponse<String> result) {
        assertNotNull(result);
        assertTrue(result.getHeaders().containsKey(Header.CONTENT_TYPE));
        assertFalse(result.getHeaders().get(Header.CONTENT_TYPE).isEmpty());
        assertTrue(result.getHeaders().getFirst(Header.CONTENT_TYPE).contains(MimeType.APPLICATION_JSON));
    }

    public static void assertResultIsErrorJson(HttpResponse<String> result) {
        assertTrue("Response is not valid " + ErrorJson.class.getSimpleName(), TestUtils.isValidErrorJson(result));
    }

    public static void assertResponseBodyNotEmpty(HttpResponse<String> response) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotEquals("", response.getBody().trim());
    }

    public static void assertContentType(String mimeType, HttpResponse<String> response) {
        assertNotNull(mimeType);
        assertNotNull(response);

        Headers headers = response.getHeaders();
        assertNotNull(headers);
        String contentType = headers.getFirst(Header.CONTENT_TYPE);
        String actualMimeType = extractMime(contentType);
        assertEquals(mimeType, actualMimeType);
    }

    public static String whichBrowser() {
        return System.getProperty(TestApp.Selenide.BROWSER, Selenide.Browser.HTMLUNIT);
    }

    public static String getTestUrl() {
        final int serverPort = Integer.parseInt(System.getProperty(App.Properties.SERVER_PORT, "8080"));
        final String localUrl;
        String runMode = System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());

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

    public static String timeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String normalizeUrl(String endpoint) {
        assertNotNull(endpoint);
        return endpoint.startsWith("http") ? endpoint : TestUtils.getTestUrl() + endpoint;
    }

    /**
     * Following needed because in may contain something like 'application/json;encoding=UTF8'
     *
     * @param contentType Content-Type header like 'application/json;encoding=UTF8'
     * @return string which contains content type without encoding
     */
    private static String extractMime(String contentType) {
        assertNotNull(contentType);

        String[] contentTypeParts = contentType.split(";");
        if (contentTypeParts.length > 1) {
            return contentTypeParts[0];
        } else {
            return contentType;
        }
    }

    private static boolean isValidErrorJson(HttpResponse<String> result) {
        String body = result.getBody();
        try {
            ErrorJson errorJson = AppUtils.GSON.fromJson(body, ErrorJson.class);
            return errorJson != null;
        } catch (Exception e) {
            return false;
        }
    }


}
