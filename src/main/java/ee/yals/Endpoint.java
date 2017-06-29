package ee.yals;

import ee.yals.utils.ApiMethod;
import ee.yals.utils.HttpMethod;

/**
 * List of application endpoints
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public class Endpoint {
    private Endpoint() {
        throw new UnsupportedOperationException("Utility class");
    }

    @ApiMethod(HttpMethod.POST)
    public static final String STORE_API = "/api/store";

    @ApiMethod(HttpMethod.GET)
    public static final String LINK_API= "/api/link/";
    public static final String LINK_API_MAPPING= "/api/link/{ident}";
    public static final String LINK_API_BASE= "/api/link";
}
