package ee.yals.test.utils;

import com.google.gson.Gson;
import ee.yals.json.ErrorJson;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertTrue;

/**
 * Useful stuff for testing
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class TestUtils {
    private static final Gson GSON = new Gson();
    private static final String JSON = "application/json";

    public static Gson gson() {
        return GSON;
    }

    public static void assertResultIsJson(MvcResult result) throws Exception {
        assertTrue("Response is not valid " + ErrorJson.class.getSimpleName(), TestUtils.isValidErrorJson(result));
    }

    private static boolean isValidErrorJson(MvcResult mvcResult) throws Exception {
        String body = mvcResult.getResponse().getContentAsString();
        try {
            ErrorJson errorJson = GSON.fromJson(body, ErrorJson.class);
            return errorJson != null;
        } catch (Exception e) {
            return false;
        }
    }
}
