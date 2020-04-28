package eu.yals.utils;

import com.google.gson.internal.Primitives;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Object for transferring other object between methods, classes and so on.
 * Able to hold any object.
 *
 * @since 2.7
 */
public class Result {
    public static final String DEFAULT_KEY = "DEFAULT_KEY";

    private final Map<String, Object> objects = new HashMap<>();

    public static Result get() {
        return new Result();
    }

    public Result write(Object o) {
        return write(DEFAULT_KEY, o);
    }

    public Result write(String key, Object o) {
        objects.put(key, o);
        return this;
    }

    public <T> T read(Class<T> classOfT) {
        return read(DEFAULT_KEY, classOfT);
    }

    public <T> T read(String key, Class<T> classOfT) {
        Object o = objects.get(key);
        return Primitives.wrap(classOfT).cast(o);
    }

    @SuppressWarnings("rawtypes")
    public Class readValueType(String key) {
        Object obj = objects.get(key);
        return Objects.isNull(obj) ? ObjectUtils.Null.class : obj.getClass();
    }

}
