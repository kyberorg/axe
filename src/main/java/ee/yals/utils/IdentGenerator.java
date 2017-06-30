package ee.yals.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public class IdentGenerator {
    private static final int IDENT_DEFAULT_LENGTH = 6;
    private static final int IDENT_MAX_LENGTH = 255;
    public static final String VALID_IDENT_PATTERN = "[a-zA-Z]{1," + IDENT_MAX_LENGTH + "}";

    private IdentGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String generateNewIdent() {
        return RandomStringUtils.randomAlphabetic(IDENT_DEFAULT_LENGTH);
    }
}
