package eu.yals.constants;

/**
 * MIME-types
 *
 * @since 2.0
 */
public final class MimeType {

    private MimeType(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_CSS = "text/css";
    public static final String OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_JSON = "application/json";
    public static final String IMAGE_X_ICON = "image/x-icon";
}
