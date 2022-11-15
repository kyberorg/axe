package io.kyberorg.yalsee.users;

import lombok.Getter;

/**
 * Type of Token with its properties.
 */
public enum TokenType {
    ACCOUNT_CONFIRMATION_TOKEN(86400, "", TokenValueType.UUID), //24h
    LOGIN_VERIFICATION_TOKEN(3600, "", TokenValueType.CODE), //1h
    PASSWORD_RESET_TOKEN(3600, "fp", TokenValueType.UUID), //1h
    USER_API_TOKEN(36000, "", TokenValueType.UUID); //10h

    @Getter
    private final int tokenAge;

    @Getter
    private final String identPrefix;

    @Getter
    private final TokenValueType valueType;

    TokenType(final int tokenAgeSeconds, final String identPrefix, final TokenValueType tokenValueType) {
        this.tokenAge = tokenAgeSeconds;
        this.identPrefix = identPrefix;
        this.valueType = tokenValueType;
    }
}
