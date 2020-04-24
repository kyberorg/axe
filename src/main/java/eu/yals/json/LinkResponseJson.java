package eu.yals.json;

import eu.yals.json.internal.Json;
import lombok.Getter;

/**
 * Store Endpoint outgoing JSON.
 *
 * @since 1.0
 */
public class LinkResponseJson extends Json {
    @Getter
    private String link;

    /**
     * Creates blank {@link LinkResponseJson} without params.
     *
     * @return empty {@link LinkResponseJson} object.
     */
    public static LinkResponseJson create() {
        return new LinkResponseJson();
    }

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
}
