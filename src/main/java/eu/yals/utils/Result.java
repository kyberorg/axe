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
    /**
     * Key, which is used as default value.
     */
    public static final String DEFAULT_KEY = "DEFAULT_KEY";

    private final Map<String, Object> objects = new HashMap<>();

    /**
     * Static constructor.
     *
     * @return new object
     */
    public static Result get() {
        return new Result();
    }

    /**
     * Stores object with {@link #DEFAULT_KEY}. If stored multiple time, value is overridden.
     *
     * @param o object to store
     * @return same {@link Result} object, but with stored value
     */
    public Result write(final Object o) {
        return write(DEFAULT_KEY, o);
    }

    /**
     * Stores object under given key. If stored multiple time under same key, value is overridden.
     *
     * @param key string with key
     * @param o   object to store
     * @return same {@link Result} object, but with stored key-value pair
     */
    public Result write(final String key, final Object o) {
        objects.put(key, o);
        return this;
    }

    /**
     * Reads object stored under {@link #DEFAULT_KEY}.
     *
     * @param classOfT type of return value
     * @param <T>      type of return value
     * @return stored object or null
     */
    public <T> T read(final Class<T> classOfT) {
        return read(DEFAULT_KEY, classOfT);
    }

    /**
     * Reads object stored under given key.
     *
     * @param key      string with key
     * @param classOfT type of return value
     * @param <T>      type of return value
     * @return stored value casted to requested type or null
     */
    public <T> T read(final String key, final Class<T> classOfT) {
        Object o = objects.get(key);
        return Primitives.wrap(classOfT).cast(o);
    }

    /**
     * Returns type of value stored under given key.
     *
     * @param key string with key
     * @return type of stored value or {@link ObjectUtils.Null} if nothing found
     */
    @SuppressWarnings("rawtypes")
    public Class readValueType(final String key) {
        Object obj = objects.get(key);
        return Objects.isNull(obj) ? ObjectUtils.Null.class : obj.getClass();
    }

}
