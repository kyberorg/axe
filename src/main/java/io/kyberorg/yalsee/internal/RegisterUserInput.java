package io.kyberorg.yalsee.internal;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Data
public class RegisterUserInput {
    private final String email;
    private final String username;

    @ToString.Exclude
    private final String password;
    private final boolean tfaEnabled;

}
