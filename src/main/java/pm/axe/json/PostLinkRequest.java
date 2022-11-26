package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import pm.axe.utils.UrlExtraValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Store Endpoint incoming JSON.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class PostLinkRequest implements AxeJson {

    public static final int IDENT_MIN_LEN = 2;
    public static final int IDENT_MAX_LEN = 255;

    @NotNull(message = "must be present")
    @Size(min = UrlExtraValidator.URL_MIN_SIZE, max = UrlExtraValidator.URL_MAX_SIZE)
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
