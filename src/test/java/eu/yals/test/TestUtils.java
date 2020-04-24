package eu.yals.test;

import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.ErrorJson;
import eu.yals.utils.AppUtils;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Useful stuff for testing
 *
 * @since 2.0
 */
@Slf4j
public class TestUtils {

    /**
     * Result contains JSON.
     *
     * @param result string with http response
     */
    public static void assertResultIsJson(final HttpResponse<String> result) {
        assertNotNull(result);
        assertTrue(result.getHeaders().containsKey(Header.CONTENT_TYPE));
        assertFalse(result.getHeaders().get(Header.CONTENT_TYPE).isEmpty());
        assertTrue(
                result.getHeaders().getFirst(Header.CONTENT_TYPE).contains(MimeType.APPLICATION_JSON));
    }

    /**
     * Result is {@link ErrorJson}.
     *
     * @param result string with http response
     */
    public static void assertResultIsErrorJson(final HttpResponse<String> result) {
        assertTrue(
                "Response is not valid " + ErrorJson.class.getSimpleName(),
                TestUtils.isValidErrorJson(result));
    }

    /**
     * HTTP response body is not empty.
     *
     * @param response string with http response
     */
    public static void assertResponseBodyNotEmpty(final HttpResponse<String> response) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotEquals("", response.getBody().trim());
    }

    /**
     * Response has given mimeType.
     *
     * @param mimeType mime type to check
     * @param response response having mime-type
     */
    public static void assertContentType(final String mimeType, final HttpResponse<String> response) {
        assertNotNull(mimeType);
        assertNotNull(response);

        Headers headers = response.getHeaders();
        assertNotNull(headers);
        String contentType = headers.getFirst(Header.CONTENT_TYPE);
        String actualMimeType = extractMime(contentType);
        assertEquals(mimeType, actualMimeType);
    }

    /**
     * Content of request is not empty.
     *
     * @param response http response
     */
    public static void assertContentNotEmpty(final HttpResponse<String> response) {
        assertNotNull(response);
        Headers headers = response.getHeaders();
        assertNotNull(headers);
        String contentLengthStr = headers.getFirst(Header.CONTENT_LENGTH);

        assertNotNull(contentLengthStr);
        int contentLength = 0;
        try {
            contentLength = Integer.parseInt(contentLengthStr);
        } catch (NumberFormatException e) {
            fail(String.format("%s header value is not a number", Header.CONTENT_LENGTH));
        }
        assertTrue("Content is empty", contentLength > 0);
    }

    /**
     * Asserts that string is empty.
     *
     * @param message test error message
     * @param string  string to check
     */
    public static void assertEmpty(String message, String string) {
        assertTrue(message, StringUtils.isBlank(string));
    }

    /**
     * Defines URL of server to run tests against.
     *
     * @return string with Server+Port
     */
    public static String getTestUrl() {
        final int serverPort = Integer.parseInt(System.getProperty(App.Properties.SERVER_PORT, "8080"));
        final String localUrl;
        String runMode =
                System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());

        if (runMode.equals(TestApp.RunMode.CONTAINER.name())) {
            localUrl = String.format("http://host.testcontainers.internal:%d", serverPort);
        } else {
            localUrl = String.format("http://localhost:%d", serverPort);
        }

        return System.getProperty(TestApp.Properties.TEST_URL, localUrl);
    }

    /**
     * Determines if tests are running locally (docker container or localhost).
     *
     * @return true if locally, false if not
     */
    public static boolean isLocalRun() {
        String testUrl = getTestUrl();
        String dockerHost = "host.testcontainers.internal";
        String localhost = "localhost";

        return (testUrl.contains(dockerHost) || testUrl.contains(localhost));
    }

    /**
     * Provides current time for test naming.
     *
     * @return string with timestamp
     */
    public static String timeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    /**
     * List of browser to run tests with.
     *
     * @return list of {@link TestApp.Browser}
     */
    public static List<TestApp.Browser> getTestBrowsers() {
        List<TestApp.Browser> browsers = new ArrayList<>(1);

        String testBrowsersProp = System.getProperty(TestApp.Properties.TEST_BROWSERS, "");
        if (StringUtils.isBlank(testBrowsersProp)) {
            return defaultBrowsers();
        }

        String[] testBrowsers = testBrowsersProp.trim().split(",");
        if (testBrowsers.length <= 0) {
            return defaultBrowsers();
        }
        for (String testBrowser : testBrowsers) {
            TestApp.Browser browser;
            try {
                browser = TestApp.Browser.valueOf(testBrowser.trim().toUpperCase());
                browsers.add(browser);
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error(String.format("Browser '%s' is not supported. Skipping...", testBrowser), e);
            }
        }
        if (browsers.isEmpty()) browsers = defaultBrowsers();
        return browsers;
    }

    private static List<TestApp.Browser> defaultBrowsers() {
        return Collections.singletonList(TestApp.Browser.CHROME);
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
            assertNotNull(errorJson);
            boolean hasNotEmptyErrorField = errorJson.getError() != null;
            boolean hasNotEmptyErrorsField = errorJson.getErrors() != null;

            return hasNotEmptyErrorField || (hasNotEmptyErrorsField && errorJson.getErrors().size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
}
