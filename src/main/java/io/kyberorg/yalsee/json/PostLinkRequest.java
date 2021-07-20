package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.kyberorg.yalsee.utils.UrlExtraValidator;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static io.kyberorg.yalsee.utils.UrlExtraValidator.URL_MAX_SIZE;
import static io.kyberorg.yalsee.utils.UrlExtraValidator.URL_MIN_SIZE;

/**
 * Store Endpoint incoming JSON.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class PostLinkRequest implements YalseeJson {

    public static final int IDENT_MIN_LEN = 2;
    public static final int IDENT_MAX_LEN = 255;

    @NotNull(message = "must be present")
    @Size(min = URL_MIN_SIZE, max = URL_MAX_SIZE)
    @URL(message = UrlExtraValidator.URL_NOT_VALID)
    @JsonProperty("link")
    private String link;

    @Size(min = IDENT_MIN_LEN, max = IDENT_MAX_LEN)
    @JsonProperty("ident")
    private String ident;

    /**
     * Creates {@link PostLinkRequest} with provided link.
     *
     * @param longLink field with long link to shorten
     * @return JSON which contains long link in {@link #link} param
     */
    public PostLinkRequest withLink(final String longLink) {
        this.link = longLink;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
