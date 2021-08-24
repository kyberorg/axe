package io.kyberorg.yalsee.test.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.kyberorg.yalsee.test.TestApp;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Useful Grid-related utils for Selenide tests.
 *
 * @since 3.2.1
 */
public final class GridUtils {

    private final String gridUrl;
    private String sessionId;

    /**
     * Creates {@link GridUtils} linked to current session.
     *
     * @param sessionId non-empty string with session identifier. For Selenide use {@link WebDriverRunner#driver()}.
     * @return {@link GridUtils} instance linked to given session.
     */
    public static GridUtils getInstance(final String sessionId) {
        GridUtils gridUtils = new GridUtils();
        gridUtils.sessionId = sessionId;
        return gridUtils;
    }

    /**
     * Provides Grid (aka Selenoid) URL constructed from application properties.
     *
     * @return string with Grid URL with protocol and grid standard postfix ({@literal /wd/hub}).
     */
    public static String getGridFullUrl() {
        final String httpsPrefix = "https://";
        final String httpPrefix = "http://";
        final String gridPostfix = "/wd/hub";

        String selenideRemote = System.getProperty(TestApp.Properties.Selenide.REMOTE, "");
        if (StringUtils.isNotBlank(selenideRemote)) {
            return selenideRemote;
        }

        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME);
        boolean hostnameStringHasProtocol = gridHostname.startsWith(httpsPrefix) || gridHostname.startsWith(httpPrefix);
        boolean hostnameStringHasGridPostfix = gridHostname.contains(gridPostfix);
        if (hostnameStringHasProtocol && hostnameStringHasGridPostfix) {
            return gridHostname;
        } else if (hostnameStringHasProtocol) {
            return gridHostname + gridPostfix;
        } else if (hostnameStringHasGridPostfix) {
            return httpPrefix + gridHostname;
        } else {
            return httpPrefix + gridHostname + gridPostfix;
        }
    }

    private static String getGridApiUrl() {
        final String gridPostfix = "/wd/hub";
        return getGridFullUrl().replaceAll(gridPostfix, "");
    }

    private GridUtils() {
        this.gridUrl = getGridApiUrl();
    }

    /**
     * Provides remote clipboard content by querying Selenoid API. If query fails - it fails test.
     *
     * @return clipboard content or empty string, if failed.
     */
    public String getClipboardValue() {
        final String endpointUrl = this.gridUrl + "/clipboard/" + this.sessionId;
        HttpResponse<String> httpResponse = Unirest.get(endpointUrl).asString();
        if (httpResponse.isSuccess()) {
            return httpResponse.getBody();
        } else {
            fail("Failed to access remote clipboard. Grid reported " + httpResponse.getStatusText());
            return "";
        }
    }

    /**
     * Cleans clipboard by sending empty string.
     */
    public void cleanClipboard() {
        pasteValueToClipboard("");
    }

    @SuppressWarnings("SameParameterValue")
    private void pasteValueToClipboard(final String value) {
        final String endpointUrl = this.gridUrl + "/clipboard/" + this.sessionId;
        HttpResponse<String> response = Unirest.post(endpointUrl).body(value).asString();
        if (!response.isSuccess()) {
            fail("Failed to access remote clipboard. Grid reported: " + response.getStatusText());
        }

    }
}
