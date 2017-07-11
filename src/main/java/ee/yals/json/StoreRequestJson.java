package ee.yals.json;

import com.google.gson.annotations.Since;
import ee.yals.json.internal.Json;
import ee.yals.utils.UrlExtraValidator;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Store Endpoint incoming JSON
 *
 * @since 1.0
 */
public class StoreRequestJson extends Json {
    @NotNull(message = "must be present")
    @Size(min = 5, max = 15613)
    @URL(message = UrlExtraValidator.URL_NOT_VALID)
    @Since(1.0)
    private String link;

    public static StoreRequestJson create() {
        return new StoreRequestJson();
    }

    public StoreRequestJson withLink(String link) {
        this.link = link;
        return this;
    }

    public String getLink() {
        return link;
    }
}
