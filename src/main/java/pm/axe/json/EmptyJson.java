package pm.axe.json;

import com.google.gson.Gson;
import lombok.Data;

/**
 * JSON without fields. Can be used for testing.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class EmptyJson implements AxeJson {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
