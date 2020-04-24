package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.yals.json.internal.Json;
import kong.unirest.HttpMethod;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * This struct of JSON send when API endpoint is not found.
 *
 * @since 2.7
 */
public class EndpointNotFoundJson extends Json {

    @Getter
    private final String error = "Endpoint not found";

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Endpoint endpoint;

    /**
     * Creates {@link EndpointNotFoundJson} from endpoint method and path.
     *
     * @param method {@link HttpMethod} method aka GET/POST...
     * @param path   string with path to endpoint
     * @return json with endpoint built from given params
     */
    public static EndpointNotFoundJson createWithEndpoint(final @NotNull HttpMethod method,
                                                          final @NotNull String path) {
        EndpointNotFoundJson self = new EndpointNotFoundJson();
        self.endpoint = Endpoint.create(method, path);
        return self;
    }

    /**
     * Creates {@link EndpointNotFoundJson} without params.
     *
     * @return {@link EndpointNotFoundJson} object with defaults.
     */
    public static EndpointNotFoundJson create() {
        return new EndpointNotFoundJson();
    }

    /**
     * method and path combination (for example: GET /path).
     */
    public static class Endpoint {
        @Getter
        private final String method;

        @Getter
        private final String path;

        /**
         * Constructs {@link Endpoint} object from method and path.
         *
         * @param httpMethod   http method like GET/POST...
         * @param endpointPath string with endpoint path
         */
        public Endpoint(final HttpMethod httpMethod, final String endpointPath) {
            this.method = httpMethod.name();
            String newPath;
            if (StringUtils.isNotBlank(endpointPath) && !endpointPath.startsWith("/")) {
                newPath = "/" + endpointPath;
            } else {
                newPath = endpointPath;
            }
            this.path = newPath;
        }

        /**
         * Static method which is analog to {@link #Endpoint(HttpMethod, String)}.
         *
         * @param method http method like GET/POST...
         * @param path   string with endpoint path
         * @return created {@link Endpoint} object
         */
        public static Endpoint create(final HttpMethod method, final String path) {
            return new Endpoint(method, path);
        }
    }
}
