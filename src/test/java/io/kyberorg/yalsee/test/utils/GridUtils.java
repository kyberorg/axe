package io.kyberorg.yalsee.test.utils;

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

    public static GridUtils getInstance(final String sessionId) {
        GridUtils gridUtils = new GridUtils();
        gridUtils.sessionId = sessionId;
        return gridUtils;
    }

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

    private GridUtils() {
        this.gridUrl = getGridFullUrl();
    }

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
}
