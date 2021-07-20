package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Store Endpoint outgoing JSON.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class PostLinkResponse implements YalseeJson {
    @JsonProperty("ident")
    private String ident;

    @JsonProperty("link")
    private String link;

    /**
     * Creates {@link PostLinkResponse} with given ident.
     *
     * @param ident string with ident.
     * @return json containing ident.
     */
    public PostLinkResponse withIdent(final String ident) {
        this.ident = ident;

        String shortUrl = AppUtils.getShortUrlFromStaticContext();
        if (StringUtils.isNotBlank(shortUrl)) {
            this.link = shortUrl + "/" + ident;
        }
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
