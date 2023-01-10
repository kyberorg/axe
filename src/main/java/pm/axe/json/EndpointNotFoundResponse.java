package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import kong.unirest.HttpMethod;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * This struct of JSON send when API endpoint is not found.
 *
 * @since 2.7
 */
@Data(staticConstructor = "create")
public class EndpointNotFoundResponse implements AxeJson {

    @JsonProperty("endpoint")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Endpoint endpoint;

    /**
     * Creates {@link EndpointNotFoundResponse} from endpoint method and path.
     *
     * @param method {@link HttpMethod} method aka GET/POST...
     * @param path   string with path to endpoint
     * @return json with endpoint built from given params
     */
    public static EndpointNotFoundResponse createWithEndpoint(final @NotNull HttpMethod method,
                                                              final @NotNull String path) {
        EndpointNotFoundResponse self = new EndpointNotFoundResponse();
        self.endpoint = Endpoint.create(method, path);
        return self;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
