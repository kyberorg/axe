package ee.yals.json;

/**
 * JSON without fields. Can be used for testing
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class EmptyJson extends Json {
    public static EmptyJson create() {
        return new EmptyJson();
    }
}
