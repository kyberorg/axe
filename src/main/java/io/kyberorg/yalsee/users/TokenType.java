package io.kyberorg.yalsee.users;

import lombok.Getter;

public enum TokenType {
    ACCOUNT_CONFIRMATION_TOKEN(86400, "ct"), //24h
    LOGIN_VERIFICATION_TOKEN(3600, ""), //1h
    PASSWORD_RESET_TOKEN(3600, "fp"), //1h
    USER_API_TOKEN(36000, ""); //10h

    @Getter
    private final int tokenAge;

    @Getter
    private final String identPrefix;

    TokenType(int tokenAgeSeconds, String identPrefix) {
        this.tokenAge = tokenAgeSeconds;
        this.identPrefix = identPrefix;
    }
}
