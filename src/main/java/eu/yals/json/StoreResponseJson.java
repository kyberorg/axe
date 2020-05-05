package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;

/**
 * Store Endpoint outgoing JSON.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class StoreResponseJson implements YalsJson {
    @JsonProperty("ident")
    private String ident;

    /**
     * Creates {@link StoreResponseJson} with given ident.
     *
     * @param ident string with ident.
     * @return json containing ident.
     */
    public StoreResponseJson withIdent(final String ident) {
        this.ident = ident;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
