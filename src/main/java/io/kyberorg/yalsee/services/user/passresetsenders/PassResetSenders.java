package io.kyberorg.yalsee.services.user.passresetsenders;

import io.kyberorg.yalsee.users.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PassResetSenders {
    private final NoOpPassResetSender noOpPassResetSender;
    private final EmailPassResetSender emailPassResetSender;

    public PassResetSender get(AuthProvider authProvider) {
        PassResetSender passResetSender;
        switch (authProvider) {
            case LOCAL:
            default:
                passResetSender = noOpPassResetSender;
                break;
            case EMAIL:
                passResetSender = emailPassResetSender;
                break;
        }
        return passResetSender;
    }
}
