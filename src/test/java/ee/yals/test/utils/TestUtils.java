package ee.yals.test.utils;

import ee.yals.constants.Header;
import ee.yals.constants.MimeType;
import ee.yals.json.ErrorJson;
import ee.yals.utils.AppUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Useful stuff for testing
 *
 * @since 2.0
 */
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
        String[] contentTypeParts = contentType.split(";");
        if (contentTypeParts.length > 1) {
            String onlyContentType = contentTypeParts[0];
            assertEquals(mimeType, onlyContentType);
        } else {
            assertEquals(mimeType, result.getResponse().getContentType());
        }
    }

    public static String whichBrowser() {
        return System.getProperty(Selenide.Props.BROWSER, Selenide.Browser.HTMLUNIT);
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
