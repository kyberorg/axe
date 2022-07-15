package io.kyberorg.yalsee.exception;

/**
 * Runtime exception that thrown when application it failed to encode URL.
 *
 * @since 3.2.1
 */
public class URLEncodeException extends RuntimeException {

    /**
     * Exception Constructor.
     *
     * @param cause UnsupportedEncodingException
     */
    public URLEncodeException(final Throwable cause) {
        super(cause);
    }
}
