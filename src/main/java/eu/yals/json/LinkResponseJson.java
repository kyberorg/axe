package eu.yals.json;

import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;

/**
 * Store Endpoint outgoing JSON
 *
 * @since 1.0
 */
public class LinkResponseJson extends Json {
    @Since(1.0)
    private String link;

    public static LinkResponseJson create() {
        return new LinkResponseJson();
    }

    public LinkResponseJson withLink(String link) {
        this.link = link;
        return this;
    }

    public String getLink() {
        return this.link;
    }
}
