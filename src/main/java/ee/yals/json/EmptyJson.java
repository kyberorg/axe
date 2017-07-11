package ee.yals.json;

import ee.yals.json.internal.Json;

/**
 * JSON without fields. Can be used for testing
 *
 * @since 1.0
 */
public class EmptyJson extends Json {
    public static EmptyJson create() {
        return new EmptyJson();
    }
}
