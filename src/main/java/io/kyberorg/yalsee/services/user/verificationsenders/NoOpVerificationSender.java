package io.kyberorg.yalsee.services.user.verificationsenders;

import io.kyberorg.yalsee.result.OperationResult;
import org.springframework.stereotype.Component;

@Component
public class NoOpVerificationSender implements VerificationSender {
    @Override
    public OperationResult sendVerification(String destination, String verificationCode) {
        return OperationResult.success();
    }
}
