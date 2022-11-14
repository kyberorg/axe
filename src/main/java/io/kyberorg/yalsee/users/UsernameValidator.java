package io.kyberorg.yalsee.users;

import io.kyberorg.yalsee.result.OperationResult;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Username Validator.
 * <p>
 * Supported Username is:
 * <p>
 * Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.
 * Username allowed of the dot (.), underscore (_), and hyphen (-).
 * The dot (.), underscore (_), or hyphen (-) must not be the first or last character.
 * The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., java..regex
 * The number of characters must be between 2 and 20.
 */
public final class UsernameValidator {
    private UsernameValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Username Pattern.
     * ^[a-zA-Z0-9]      # start with an alphanumeric character
     * (                 # start of (group 1)
     * [._-](?![._-])  # follow by a dot, hyphen, or underscore, negative lookahead to
     * # ensures dot, hyphen, and underscore does not appear consecutively
     * |               # or
     * [a-zA-Z0-9]     # an alphanumeric character
     * )                 # end of (group 1)
     * {0,18}            # ensures the length of (group 1) between 0 and 18
     * [a-zA-Z0-9]$      # end with an alphanumeric character
     * <p>
     * # {0,18} plus the first and last alphanumeric characters,
     * # total length became {2,20}
     */
    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,18}[a-zA-Z0-9]$";

    private static final Pattern COMPILED_PATTERN = Pattern.compile(USERNAME_PATTERN);

    private static final int USERNAME_MAX_LENGTH = 100;
    private static final String ERR_EMPTY_USERNAME = "Username cannot be empty";
    private static final String ERR_NOT_VALID_CHARS_IN_USERNAME = "There are non-valid chars in username";
    private static final String ERR_USERNAME_IS_TOO_LONG = "Username is too long";

    /**
     * Tests username on username requirements.
     *
     * @param username not-empty string with username to validate.
     * @return true - if username meets requirements, false - if not.
     */
    public static OperationResult isValid(final String username) {
        if (StringUtils.isBlank(username)) {
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_USERNAME);
        }
        Matcher matcher = COMPILED_PATTERN.matcher(username);
        if (!matcher.matches()) {
            return OperationResult.malformedInput().withMessage(ERR_NOT_VALID_CHARS_IN_USERNAME);
        }
        if (username.length() > USERNAME_MAX_LENGTH) {
            return OperationResult.malformedInput().withMessage(ERR_USERNAME_IS_TOO_LONG);
        }
        return OperationResult.success();
    }
}
