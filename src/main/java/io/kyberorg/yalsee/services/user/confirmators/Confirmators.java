package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public final class Confirmators {

    private final NoOpConfirmator noOpConfirmator;
    private final EmailConfirmator emailConfirmator;

    public Confirmator get(AuthProvider authProvider) {
        Confirmator confirmator;
        switch (authProvider) {
            case LOCAL:
            default:
                confirmator = noOpConfirmator;
                break;
            case EMAIL:
                confirmator = emailConfirmator;
                break;
        }
        return confirmator;
    }
}
