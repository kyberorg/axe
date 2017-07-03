package ee.yals.json;

import com.google.gson.annotations.Since;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Store Endpoint incoming JSON
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class StoreJson extends Json {
    @NotNull(message = "must be present")
    @Size(min = 5, max = 15613)
    @URL
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
