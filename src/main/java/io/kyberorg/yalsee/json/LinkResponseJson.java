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
public class LinkResponseJson implements YalsJson {
    @JsonProperty("link")
    private String link;

    /**
     * Creates {@link LinkResponseJson} with given link param.
     *
     * @param shortLink string with short link
     * @return {@link LinkResponseJson} containing link param.
     */
    public LinkResponseJson withLink(final String shortLink) {
        this.link = shortLink;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
