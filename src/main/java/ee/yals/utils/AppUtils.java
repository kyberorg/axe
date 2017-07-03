package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
}
