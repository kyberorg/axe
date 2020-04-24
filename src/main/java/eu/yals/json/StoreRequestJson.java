package eu.yals.json;

import eu.yals.json.internal.Json;
import eu.yals.utils.UrlExtraValidator;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.yals.utils.UrlExtraValidator.URL_MAX_SIZE;
import static eu.yals.utils.UrlExtraValidator.URL_MIN_SIZE;

/**
 * Store Endpoint incoming JSON.
 *
 * @since 1.0
 */
public class StoreRequestJson extends Json {
    @NotNull(message = "must be present")
    @Size(min = URL_MIN_SIZE, max = URL_MAX_SIZE)
    @URL(message = UrlExtraValidator.URL_NOT_VALID)
    @Getter
    private String link;

    /**
     * Creates blank {@link StoreRequestJson} with no params.
     *
     * @return json object with no params
     */
    public static StoreRequestJson create() {
        return new StoreRequestJson();
    }

    /**
     * Creates {@link StoreRequestJson} with provided link.
     *
     * @param longLink field with long link to shorten
     * @return JSON which contains long link in {@link #link} param
     */
    public StoreRequestJson withLink(final String longLink) {
        this.link = longLink;
        return this;
    }
}
