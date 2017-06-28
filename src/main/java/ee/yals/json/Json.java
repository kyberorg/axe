package ee.yals.json;

import com.google.gson.Gson;

/**
 *
 * Basic methods for application JSONs
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class Json {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
