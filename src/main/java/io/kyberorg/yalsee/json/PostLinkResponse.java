package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;

/**
 * Store Endpoint outgoing JSON.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class PostLinkResponse implements YalseeJson {
    @JsonProperty("ident")
    private String ident;

    /**
     * Creates {@link PostLinkResponse} with given ident.
     *
     * @param ident string with ident.
     * @return json containing ident.
     */
    public PostLinkResponse withIdent(final String ident) {
        this.ident = ident;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
