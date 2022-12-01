package pm.axe.core;

import org.apache.commons.lang3.RandomStringUtils;
import pm.axe.constants.App;
import pm.axe.db.models.Token;
import pm.axe.users.TokenType;

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

    private static final int TOKEN_SUBSTRING_START_INDEX = 0;
    private static final int TOKEN_SUBSTRING_END_INDEX = 7;

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

    /**
     * Generates ident, based on {@link TokenType}. Method not checking if ident already exists.
     *
     * @param token token for getting ident prefix.
     * @return ident prefix plus random part.
     */
    public static String generateTokenIdent(final Token token) {
        if (token.getTokenType() == TokenType.ACCOUNT_CONFIRMATION_TOKEN) {
            return generateAccountConfirmationIdent(token);
        }
        String prefix = token.getTokenType().getIdentPrefix();
        String number = RandomStringUtils.randomNumeric(App.FOUR);
        String letter = RandomStringUtils.randomAlphabetic(1);
        return String.join("", prefix, number, letter);
    }

    private static String generateAccountConfirmationIdent(final Token token) {
        String prefix = token.getTokenType().getIdentPrefix();
        String firstPartOfToken = token.getToken().substring(TOKEN_SUBSTRING_START_INDEX, TOKEN_SUBSTRING_END_INDEX);
        return String.join("", prefix, firstPartOfToken);
    }
}
