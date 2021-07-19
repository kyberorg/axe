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
public class GetLinkResponse implements YalseeJson {
    @JsonProperty("link")
    private String link;

    /**
     * Creates {@link GetLinkResponse} with given link param.
     *
     * @param shortLink string with short link
     * @return {@link GetLinkResponse} containing link param.
     */
    public GetLinkResponse withLink(final String shortLink) {
        this.link = shortLink;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
