package ee.yals.json;

import com.google.gson.annotations.Since;

/**
 * Store Endpoint outcoming JSON
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class LinkReplyJson extends Json {
    @Since(1.0)
    private String link;

    public static LinkReplyJson create() {
        return new LinkReplyJson();
    }

    public LinkReplyJson withLink(String link) {
        this.link = link;
        return this;
    }

    public String getLink() {
        return this.link;
    }
}
