package pm.axe.test.utils;

import com.codeborne.selenide.SelenideElement;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import kong.unirest.MimeTypes;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import pm.axe.Axe;
import pm.axe.json.AxeErrorJson;
import pm.axe.test.TestApp;
import pm.axe.test.TestApp.RunMode;
import pm.axe.test.TestedEnv;
import pm.axe.utils.AppUtils;
import pm.axe.utils.UrlUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Useful stuff for testing.
 *
 * @since 2.0
 */
public final class TestUtils {
    private static final String QR_CODE_MARKER = "data:image/png;base64";

    private TestUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Result contains JSON.
     *
     * @param result string with http response
     */
    public static void assertResultIsJson(final HttpResponse<String> result) {
        assertNotNull(result);
        assertTrue(result.getHeaders().containsKey(Axe.Headers.CONTENT_TYPE));
        assertFalse(result.getHeaders().get(Axe.Headers.CONTENT_TYPE).isEmpty());
        assertTrue(
                result.getHeaders().getFirst(Axe.Headers.CONTENT_TYPE).contains(MimeTypes.JSON));
    }

    /**
     * Result is {@link AxeErrorJson}.
     *
     * @param result string with http response
     */
    public static void assertResultIsAxeErrorJson(final HttpResponse<String> result) {
        assertTrue(
                TestUtils.isValidErrorJson(result),
                "Response is not valid " + AxeErrorJson.class.getSimpleName());
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
        String contentType = headers.getFirst(Axe.Headers.CONTENT_TYPE);
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
        String contentLengthStr = headers.getFirst(Axe.Headers.CONTENT_LENGTH);

        assertNotNull(contentLengthStr);
        int contentLength = 0;
        try {
            contentLength = Integer.parseInt(contentLengthStr);
        } catch (NumberFormatException e) {
            fail(String.format("%s header value is not a number", Axe.Headers.CONTENT_LENGTH));
        }
        assertTrue(contentLength > 0, "Content is empty");
    }

    /**
     * Assert Vaadin Element has given theme.
     * The reason this method needed is that <b>theme</b> attribute can have
     * multi values in single string like "primary small".
     *
     * @param element    themed element
     * @param themeValue string with theme to search
     */
    public static void assertHasTheme(final SelenideElement element, final String themeValue) {
        String attributeValue = element.getAttribute("theme");
        assert attributeValue != null;
        String[] allThemes = attributeValue.split(" ");
        boolean hasTheme = false;
        for (String theme : allThemes) {
            if (theme.equals(themeValue)) {
                hasTheme = true;
                break;
            }
        }
        assertTrue(hasTheme, "Element has no theme " + themeValue);
    }

    /**
     * Defines URL of server to run tests against.
     *
     * @return string with Server+Port
     */
    public static String getTestUrl() {
        final int serverPort = Integer.parseInt(System.getProperty(Axe.Properties.SERVER_PORT, "8080"));
        final String localUrl;

        if (StringUtils.isNotBlank(System.getProperty(TestApp.Properties.TEST_URL, ""))) {
            return System.getProperty(TestApp.Properties.TEST_URL);
        }

        RunMode runMode = RunMode.valueOf(
                System.getProperty(TestApp.Properties.TEST_RUN_MODE, RunMode.LOCAL.name()));

        localUrl = (runMode == RunMode.GRID)
                ? System.getProperty(TestApp.Properties.TEST_URL)
                : String.format("http://localhost:%d", serverPort);

        return localUrl;
    }

    /**
     * Defines short URL App Uses for Links.
     *
     * @return string with URL app includes in short links
     */
    public static String getAppShortUrl() {
        final int serverPort = Integer.parseInt(System.getProperty(Axe.Properties.SERVER_PORT, "8080"));
        String localRunValue = String.format("http://l.axe.pm:%d", serverPort);

        TestedEnv testedEnv = getTestedEnv();
        return (testedEnv == TestedEnv.LOCAL) ? localRunValue : testedEnv.getShortUrl();
    }

    /**
     * Same as {@link #getAppShortUrl()}, but without schema.
     *
     * @return string with short domain of tested instance.
     */
    public static String getAppShortDomain() {
        return UrlUtils.removeProtocol(getAppShortUrl());
    }

    /**
     * Determines if tests are running locally (localhost).
     *
     * @return true if locally, false if not
     */
    public static boolean isLocalRun() {
        String testUrl = getTestUrl();
        String localhost = "localhost";

        return testUrl.contains(localhost);
    }

    /**
     * Hostname of executing machine.
     *
     * @return string with hostname
     */
    public static String hostName() {
        return HostIdentifier.getHostName();
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
     * Provides current test environment. Based on test url.
     *
     * @return env, based on {@link TestApp.Properties#TEST_URL} property. Default {@link TestedEnv#LOCAL}
     */
    public static TestedEnv getTestedEnv() {
        return TestedEnv.getByTestUrl(getTestUrl());
    }

    /**
     * Provides delete token from test properties.
     *
     * @return string with token or {@link Axe.C#NO_VALUE}
     */
    public static String getDeleteToken() {
        return System.getProperty(TestApp.Properties.TEST_MASTER_TOKEN, Axe.C.NO_VALUE);
    }

    /**
     * Provides redirect page bypass symbol based on {@link TestedEnv} settings.
     *
     * @return string with bypass symbol
     */
    public static String addRedirectPageBypassSymbol() {
        return getTestedEnv().getRedirectPageBypassSymbol();
    }

    /**
     * Determines if image source is QR code. It just evaluates if it has {@link #QR_CODE_MARKER} or not.
     *
     * @param imageSource string with src attribute content.
     * @return true if image source contains QR Code, false otherwise.
     */
    public static boolean isQRCode(final String imageSource) {
        return StringUtils.isNotBlank(imageSource) && imageSource.contains(QR_CODE_MARKER);
    }


    /**
     * Gets list of test name from given test class.
     *
     * @param testClass test class to scan
     * @return list of test method names
     */
    public static List<String> getAllTestNames(final Class<?> testClass) {
        List<Method> testMethods = getMethodsAnnotatedWith(testClass, Test.class);
        List<String> testNames = new ArrayList<>();
        for (Method m : testMethods) {
            testNames.add(m.getName());
        }
        return testNames;
    }

    /**
     * Get list of Methods what are annotated with given annotation.
     *
     * @param type       class to scan
     * @param annotation annotation to search
     * @return list of methods.
     */
    @SuppressWarnings("SameParameterValue") //Currently, used for Test.class only
    private static List<Method> getMethodsAnnotatedWith(final Class<?> type,
                                                        final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<>();
        Class<?> klass = type;
        while (klass != Object.class) {
            // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable,
            // and add those annotated with the specified annotation
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }

    /**
     * Following needed because it may contain something like 'application/json;encoding=UTF8'.
     *
     * @param contentType Content-Type header like 'application/json;encoding=UTF8'
     * @return string which contains content type without encoding
     */
    private static String extractMime(final String contentType) {
        assertNotNull(contentType);

        String[] contentTypeParts = contentType.split(";");
        if (contentTypeParts.length > 1) {
            return contentTypeParts[0];
        } else {
            return contentType;
        }
    }

    private static boolean isValidErrorJson(final HttpResponse<String> result) {
        String body = result.getBody();
        try {
            AxeErrorJson errorJson = AppUtils.GSON.fromJson(body, AxeErrorJson.class);
            assertNotNull(errorJson);
            boolean hasNotEmptyMessageField = errorJson.getMessage() != null;
            boolean hasNotEmptyTimestampField = errorJson.getTimestamp() != null;

            return hasNotEmptyMessageField || hasNotEmptyTimestampField;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
    }
}
