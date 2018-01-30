package ee.yals.json;

import com.google.gson.annotations.Since;
import ee.yals.json.internal.Json;

/**
 * Class description
 *
 * @since 2.3
 */
public class MattermostResponseJson extends Json {
    @Since(1.0)
    public String iconUrl = "https://yals.ee/favicon.ico";

    public String text;

    public String responseType = "in_channel";

    public String gotoLocation;

    public String username = "YalsBot";

}
