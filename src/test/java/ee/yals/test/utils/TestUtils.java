package ee.yals.test.utils;

import ee.yals.constants.Header;
import ee.yals.constants.MimeType;
import ee.yals.json.ErrorJson;
import ee.yals.utils.AppUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

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
        assertEquals(mimeType, result.getResponse().getContentType());
    }

    public static void assertErrorMessageContainsText(ErrorJson errorJson, String text) {
        assertTrue("Given object is not a valid ErrorJson", isValidErrorJson(errorJson));
        assertNotNull("No error found in " + ErrorJson.class.getSimpleName(), errorJson.getError());
        String message = errorJson.getError().getErrorMessage();
        assertTrue("No message found in " + ErrorJson.class.getSimpleName(), Objects.nonNull(message));
        assertTrue("Given text wasn't found", message.contains(text));
    }

    public static void assertMockMvcAvailable(MockMvc mockMvc) {
        assertNotNull("MockMvc is not available", mockMvc);
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

    private static boolean isValidErrorJson(Object errorJson) {
        return !Objects.isNull(errorJson) && errorJson instanceof ErrorJson && !((ErrorJson) errorJson).equalsToEmptyObject();

    }
}
