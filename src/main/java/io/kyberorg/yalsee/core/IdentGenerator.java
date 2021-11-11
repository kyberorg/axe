package io.kyberorg.yalsee.core;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Generates unique combination for short URL aka ident.
 *
 * @since 1.0
 */
public final class IdentGenerator {
    private static final int IDENT_DEFAULT_LENGTH = 6;
    private static final int IDENT_MAX_LENGTH = 255;
    public static final String VALID_IDENT_PATTERN = "[a-zA-Z0-9]{2," + IDENT_MAX_LENGTH + "}";

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
}
