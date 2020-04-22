package eu.yals.json;

import eu.yals.json.internal.Json;

/**
 * JSON without fields. Can be used for testing.
 *
 * @since 1.0
 */
public class EmptyJson extends Json {
    /**
     * Creates empty json without fields.
     *
     * @return empty json.
     */
    public static EmptyJson create() {
        return new EmptyJson();
    }
}
