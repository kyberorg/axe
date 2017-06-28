package ee.yals.json;

import com.google.gson.annotations.Since;

/**
 * Store Endpoint incoming JSON
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class StoreJson extends Json {
    @Since(1.0)
    private String link;

    public static StoreJson create() {
        return new StoreJson();
    }

    public StoreJson withLink(String link) {
        this.link = link;
        return this;
    }

    public String getLink() {
        return link;
    }
}
