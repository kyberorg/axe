package ee.yals;

import java.util.Objects;

import static ee.yals.documentation.HttpMethod.GET;
import static ee.yals.documentation.HttpMethod.POST;

/**
 * List of application endpoints
 *
 * @since 3.0
 */
public class Endpoints {
    private Endpoints() {
    }

    private static Endpoints INSTANCE;

    public static Endpoints getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Endpoints();
        }
        return INSTANCE;
    }

    public static final EndpointN STORE_API = EndpointN.create(POST, "/api/store").asAPIEndpoint();

    public static final EndpointN LINK_API = EndpointN.create(GET, "/api/link/").asAPIEndpoint();
    public static final EndpointN LINK_API_MAPPING = EndpointN.create(GET, "/api/link/{ident}").asAPIEndpoint();
    public static final EndpointN LINK_API_BASE = EndpointN.create(GET, "/api/link/").asAPIEndpoint();

    public static final EndpointN SLASH_BASE = EndpointN.create(GET, "/");
    public static final EndpointN SLASH = EndpointN.create(GET, "/{ident}");

    public static final EndpointN ROBOTS_TXT = EndpointN.create(GET, "/robots.txt");
    public static final EndpointN HUMANS_TXT = EndpointN.create(GET, "/humans.txt");
    public static final EndpointN FAVICON_ICO = EndpointN.create(GET, "/favicon.ico");

    public static final EndpointN TEST_CSS = EndpointN.create(GET, "/s/css/test.css");

    public static final EndpointN AUTH_PAGE = EndpointN.create(GET, "/auth");
    public static final EndpointN LOGIN_PAGE = EndpointN.create(GET, "/auth/login");


    public static final EndpointN LOGIN_AUTH_API = EndpointN.create(POST, "/api/auth").asAPIEndpoint();
}
