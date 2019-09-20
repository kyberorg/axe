package eu.yals.json;

import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;

/**
 * Store Endpoint outcoming JSON
 *
 * @since 1.0
 */
public class StoreResponseJson extends Json {
    @Since(1.0)
    private String ident;

    public static StoreResponseJson create() {
        return new StoreResponseJson();
    }

    public StoreResponseJson withIdent(String ident) {
        this.ident = ident;
        return this;
    }

    public String getIdent() {
        return this.ident;
    }
}
