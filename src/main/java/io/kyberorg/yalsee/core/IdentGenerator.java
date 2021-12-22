package io.kyberorg.yalsee.core;

import io.kyberorg.yalsee.users.TokenType;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Generates unique combination for short URL aka ident.
 *
 * @since 1.0
 */
public final class IdentGenerator {
    public static final int IDENT_DEFAULT_LENGTH = 6;
    private static final int IDENT_MAX_LENGTH = 255;
    private static final int IDENT_MAX_LENGTH_WITHOUT_FIRST_AND_LAST_CHARS = IDENT_MAX_LENGTH - 2;

    /**
     * Ident pattern.
     * Regexp explanation:
     * ^[a-zA-Z0-9]      # start with an alphanumeric character
     * (                 # start of (group 1)
     * [._-](?![._-])  # follow by a dot, hyphen, or underscore, negative lookahead to
     * # ensures dot, hyphen, and underscore does not appear consecutively
     * |               # or
     * [a-zA-Z0-9]     # an alphanumeric character
     * )                 # end of (group 1)
     * {0,IDENT_MAX_LENGTH - 2}  # ensures the length of (group 1) between 0 and IDENT_MAX_LENGTH - 2
     * [a-zA-Z0-9]$      # end with an alphanumeric character
     * <p>
     * # {0,IDENT_MAX_LENGTH - 2} plus the first and last alphanumeric characters,
     * # total length became {2, IDENT_MAX_LENGTH}
     */
    public static final String VALID_IDENT_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,"
            + IDENT_MAX_LENGTH_WITHOUT_FIRST_AND_LAST_CHARS + "}[a-zA-Z0-9]$";

    private IdentGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generates ident. Method not checking if ident already exists.
     *
     * @return random {@link #IDENT_DEFAULT_LENGTH}-char string
     */
    public static String generateNewIdent() {
        return RandomStringUtils.randomAlphabetic(IDENT_DEFAULT_LENGTH);
    }

    public static String generateTokenIdent(TokenType tokenType) {
        String prefix = tokenType.getIdentPrefix();
        String number = RandomStringUtils.randomNumeric(4);
        String letter = RandomStringUtils.randomAlphabetic(1);

        return String.join("", prefix, number, letter);
    }
}
