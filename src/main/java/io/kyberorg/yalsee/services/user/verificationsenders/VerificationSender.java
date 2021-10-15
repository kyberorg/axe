package io.kyberorg.yalsee.services.user.verificationsenders;

import io.kyberorg.yalsee.result.OperationResult;

public interface VerificationSender {
    OperationResult sendVerification(String destination, String verificationCode);
}
