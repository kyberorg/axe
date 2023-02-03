package pm.axe.users;

import org.apache.commons.lang3.StringUtils;
import pm.axe.result.OperationResult;

/**
 * Passwords Validator.
 * <p>
 * Supported Password is:
 * <p>
 * Not Empty
 * Contains 3+ chars
 */
public final class PasswordValidator {
    public static final int PASSWORD_MIN_LENGTH = 3;
    public static final int PASSWORD_MAX_LENGTH = 71; //BCrypt limitation

    public static final String ERR_EMPTY_PASSWORD = "Password cannot be empty";
    public static final String ERR_SHORT_PASSWORD =
            String.format("Password is very short. Minimum %d symbols", PASSWORD_MIN_LENGTH);
    public static final String ERR_LONG_PASSWORD =
            String.format("Password is too long. Max %d symbols", PASSWORD_MAX_LENGTH);

    private PasswordValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Validates Password.
     *
     * @param password string with password to check.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error amd message.
     */
    public static OperationResult isPasswordValid(final String password) {
        if (StringUtils.isBlank(password)) {
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_PASSWORD);
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            return OperationResult.malformedInput().withMessage(ERR_SHORT_PASSWORD);
        }
        if (password.length() > PASSWORD_MAX_LENGTH) {
            return OperationResult.malformedInput().withMessage(ERR_LONG_PASSWORD);
        }

        return OperationResult.success();
    }
}
