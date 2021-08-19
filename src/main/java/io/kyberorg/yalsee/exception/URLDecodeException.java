package io.kyberorg.yalsee.exception;

/**
 * Runtime exception that thrown when application it failed to decode URL.
 *
 * @since 3.2.1
 */
public class URLDecodeException extends RuntimeException {
    /**
     * Exception Constructor.
     *
     * @param cause UnsupportedEncodingException
     */
    public URLDecodeException(Throwable cause) {
        super(cause);
    }
}
