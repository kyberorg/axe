package eu.yals.json;

import eu.yals.json.internal.Json;
import lombok.Getter;

/**
 * Store Endpoint outgoing JSON.
 *
 * @since 1.0
 */
public class StoreResponseJson extends Json {
    @Getter
    private String ident;

    /**
     * Creates empty {@link StoreResponseJson}.
     *
     * @return {@link StoreResponseJson} with no params
     */
    public static StoreResponseJson create() {
        return new StoreResponseJson();
    }

    /**
     * Creates {@link StoreResponseJson} with given ident.
     *
     * @param identStr string with ident.
     * @return json containing ident.
     */
    public StoreResponseJson withIdent(final String identStr) {
        this.ident = identStr;
        return this;
    }
}
