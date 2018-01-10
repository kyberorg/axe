package ee.yals;

import ee.yals.documentation.HttpMethod;
import org.apache.commons.lang3.StringUtils;

/**
 * Application part with something useful
 *
 * @since 3.0
 */
public class EndpointN {

    private String path;
    private boolean isApi = false;
    private HttpMethod method;

    private static final String WRONG_PATH = "wrongPath";

    public static EndpointN create(HttpMethod method, String path) {
        EndpointN endpoint = new EndpointN();
        endpoint.method = method;
        endpoint.path = StringUtils.isNoneBlank(path) ? path : WRONG_PATH;
        return endpoint;
    }

    public EndpointN asAPIEndpoint() {
        this.isApi = true;
        return this;
    }

    public String path() {
        return path;
    }

    public boolean isApi() {
        return isApi;
    }

    public HttpMethod method() {
        return method;
    }
}
