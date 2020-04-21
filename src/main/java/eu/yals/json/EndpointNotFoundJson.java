package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;
import kong.unirest.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * This struct of JSON send when API endpoint is not found
 *
 * @since 2.7
 */
@Since(2.7)
public class EndpointNotFoundJson extends Json {

    @Since(2.7)
    private final String error = "Endpoint not found";

    @Since(2.7)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Endpoint endpoint;

    public static EndpointNotFoundJson createWithEndpoint(@NotNull HttpMethod method, @NotNull String path) {
        EndpointNotFoundJson self = new EndpointNotFoundJson();
        self.endpoint = Endpoint.create(method, path);
        return self;
    }

    public static EndpointNotFoundJson create() {
        return new EndpointNotFoundJson();
    }

    @Since(2.7)
    public static class Endpoint {
        @Since(2.7)
        String method;

        @Since(2.7)
        String path;

        public Endpoint(HttpMethod method, String path) {
            this.method = method.name();
            if (StringUtils.isNotBlank(path) && !path.startsWith("/")) {
                path = "/" + path;
            }
            this.path = path;
        }

        public static Endpoint create(HttpMethod method, String path) {
            return new Endpoint(method, path);
        }
    }
}
