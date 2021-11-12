package io.kyberorg.yalsee.constants;

/**
 * List of used HTTP response code. Used to prevent magic number issue.
 *
 * @since 2.7
 */
public final class HttpCode {

    /**
     * 200 - ok.
     */
    public static final int STATUS_200 = 200;

    /**
     * 201 - created.
     */
    public static final int STATUS_201 = 201;

    /**
     * 204 - no content.
     */
    public static final int STATUS_204 = 204;

    /**
     * 301 - permanent redirect.
     */
    public static final int STATUS_301 = 301;

    /**
     * 302 - temp redirect.
     */
    public static final int STATUS_302 = 302;

    /**
     * 400 - bad request.
     */
    public static final int STATUS_400 = 400;

    /**
     * 401 - Unauthorized.
     */
    public static final int STATUS_401 = 401;

    /**
     * 403 - forbidden.
     */
    public static final int STATUS_403 = 403;

    /**
     * 404 - not found.
     */
    public static final int STATUS_404 = 404;

    /**
     * 405 - Method not allowed.
     */
    public static final int STATUS_405 = 405;

    /**
     * 406 - Not acceptable.
     */
    public static final int STATUS_406 = 406;

    /**
     * 409 - conflict.
     */
    public static final int STATUS_409 = 409;

    /**
     * 415 - Unsupported Media Type.
     */
    public static final int STATUS_415 = 415;

    /**
     * 422 - unprocessable entry.
     */
    public static final int STATUS_422 = 422;

    /**
     * 500 - server error.
     */
    public static final int STATUS_500 = 500;

    /**
     * 501 - not implemented.
     */
    public static final int STATUS_501 = 501;

    /**
     * 503 - application is down.
     */
    public static final int STATUS_503 = 503;

    private HttpCode() {
        throw new UnsupportedOperationException("Utility class");
    }
}
