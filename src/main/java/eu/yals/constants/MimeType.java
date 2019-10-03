package eu.yals.constants;

/**
 * MIME-types
 *
 * @since 2.0
 */
public final class MimeType {

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_CSS = "text/css";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String IMAGE_X_ICON = "image/x-icon";

    private MimeType() {
        throw new UnsupportedOperationException("Utility class");
    }
}
