package eu.yals.json.internal;

import com.google.gson.Gson;

/**
 *
 * Basic methods for application JSONs
 *
 * @since 2.0
 */
public abstract class Json {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
