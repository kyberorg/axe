package io.kyberorg.yalsee.users;

import io.kyberorg.yalsee.result.OperationResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Passwords Validator.
 * <p>
 * Supported Password is:
 * <p>
 * Not Empty
 * Contains 3+ chars
 */
public class PasswordValidator {
    private static final int PASSWORD_MIN_LENGTH = 3;
    private static final String ERR_EMPTY_PASSWORD = "Password cannot be empty";
    private static final String ERR_SHORT_PASSWORD = "Password is too short";

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
        return OperationResult.success();
    }
}
