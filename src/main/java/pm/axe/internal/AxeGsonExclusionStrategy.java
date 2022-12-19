package pm.axe.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * {@link Gson}'s {@link ExclusionStrategy} that ignores fields with {@link ExcludeFromJson} annotation.
 *
 * Use {@link GsonBuilder#addSerializationExclusionStrategy(ExclusionStrategy)} to add it.
 */
public final class AxeGsonExclusionStrategy {
    private AxeGsonExclusionStrategy() {
        throw new UnsupportedOperationException("utility class");
    }

    /**
     * Provides {@link ExclusionStrategy} that ignores fields annotated with {@link ExcludeFromJson}.
     *
     * @return {@link ExclusionStrategy} that ignores fields annotated with {@link ExcludeFromJson}.
     */
    public static ExclusionStrategy get() {
        return new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(final FieldAttributes field) {
                return field.getAnnotation(ExcludeFromJson.class) != null;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> aClass) {
                return false;
            }
        };
    }
}
