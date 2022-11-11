package io.kyberorg.yalsee.internal;

public record RegisterUserInput(String email, String username, String password, boolean tfaEnabled) {
}
