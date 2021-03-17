package io.kyberorg.yalsee.json;

import com.google.gson.Gson;
import lombok.Data;

/**
 * JSON without fields. Can be used for testing.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class EmptyJson implements YalseeJson {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
