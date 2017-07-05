package ee.yals.utils.constants;

/**
 * MIME-types
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public final class MimeType {

    private MimeType(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_CSS = "text/css";
    public static final String OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_JSON = "application/json";
}
