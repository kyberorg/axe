package io.kyberorg.yalsee.services.user.passresetsenders;

import io.kyberorg.yalsee.result.OperationResult;

public interface PassResetSender {
    OperationResult sendResetLink(String destination, String verificationCode);
}
