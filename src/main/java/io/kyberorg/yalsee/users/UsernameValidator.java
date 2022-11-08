package io.kyberorg.yalsee.users;

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
public class UsernameValidator {
    /**
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

    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    /**
     * Tests username on username requirements.
     *
     * @param username not-empty string with username to validate.
     * @return true - if username meets requirements, false - if not.
     */
    public static boolean isValid(final String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
