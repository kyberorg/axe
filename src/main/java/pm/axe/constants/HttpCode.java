package pm.axe.constants;

/**
 * List of used HTTP response code. Used to prevent magic number issue.
 *
 * @since 2.7
 */
public final class HttpCode {

    /**
     * 200 - ok.
     */
    public static final int OK = 200;

    /**
     * 201 - created.
     */
    public static final int CREATED = 201;

    /**
     * 204 - no content.
     */
    public static final int NO_CONTENT = 204;

    /**
     * 307 - temp redirect.
     */
    public static final int TEMPORARY_REDIRECT = 307;

    /**
     * 400 - bad request.
     */
    public static final int BAD_REQUEST = 400;

    /**
     * 401 - Unauthorized.
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 403 - forbidden.
     */
    public static final int FORBIDDEN = 403;

    /**
     * 404 - not found.
     */
    public static final int NOT_FOUND = 404;

    /**
     * 405 - Method not allowed.
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * 406 - Not acceptable.
     */
    public static final int NOT_ACCEPTABLE = 406;

    /**
     * 409 - conflict.
     */
    public static final int CONFLICT = 409;

    /**
     * 415 - Unsupported Media Type.
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    /**
     * 422 - unprocessable entry.
     */
    public static final int UNPROCESSABLE_ENTRY = 422;

    /**
     * 500 - server error.
     */
    public static final int SERVER_ERROR = 500;

    /**
     * 501 - not implemented.
     */
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * 503 - application is down.
     */
    public static final int APP_IS_DOWN = 503;

    private HttpCode() {
        throw new UnsupportedOperationException("Utility class");
    }
}
