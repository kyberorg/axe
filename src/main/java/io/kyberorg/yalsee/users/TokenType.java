package io.kyberorg.yalsee.users;

import lombok.Getter;

public enum TokenType {
    ACCOUNT_CONFIRMATION_TOKEN(86400), //24h
    LOGIN_VERIFICATION_TOKEN(3600), //1h
    PASSWORD_RESET_TOKEN(3600), //1h
    USER_API_TOKEN(36000); //10h

    @Getter
    private final int tokenAge;

    TokenType(int tokenAgeSeconds) {
        this.tokenAge = tokenAgeSeconds;
    }
}
