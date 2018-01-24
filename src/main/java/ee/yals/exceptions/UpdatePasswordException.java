package ee.yals.exceptions;

/**
 * Using to report problem with updating password with encrypted in {@link ee.yals.models.dao.SecretDao}
 *
 * @since 3.0
 */
public class UpdatePasswordException extends RuntimeException {
    public UpdatePasswordException(String message) {
        super(message);
    }

    public UpdatePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
