package ee.yals.json;

import com.google.gson.annotations.Since;

/**
 * Store Endpoint outcoming JSON
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class StoreReplyJson extends Json {
    @Since(1.0)
    private String ident;

    public static StoreReplyJson create() {
        return new StoreReplyJson();
    }

    public StoreReplyJson withIdent(String ident) {
        this.ident = ident;
        return this;
    }

    public String getIdent() {
        return this.ident;
    }
}
