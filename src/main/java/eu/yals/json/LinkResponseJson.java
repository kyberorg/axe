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
     * @param link string with short link
     * @return {@link LinkResponseJson} containing link param.
     */
    public LinkResponseJson withLink(String link) {
        this.link = link;
        return this;
    }
}
