package io.kyberorg.yalsee.services.user.verificationsenders;

import io.kyberorg.yalsee.users.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerificationSenders {
    private final NoOpVerificationSender noOpVerificationSender;
    private final EmailVerificationSender emailVerificationSender;

    public VerificationSender get(AuthProvider authProvider) {
        VerificationSender verificationSender;
        switch (authProvider) {
            case LOCAL:
            default:
                verificationSender = noOpVerificationSender;
                break;
            case EMAIL:
                verificationSender = emailVerificationSender;
                break;
        }
        return verificationSender;
    }
}
