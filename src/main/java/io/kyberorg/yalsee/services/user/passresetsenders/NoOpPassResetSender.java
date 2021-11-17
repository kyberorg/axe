package io.kyberorg.yalsee.services.user.passresetsenders;

import io.kyberorg.yalsee.result.OperationResult;
import org.springframework.stereotype.Component;

@Component
public class NoOpPassResetSender implements PassResetSender {
    @Override
    public OperationResult sendResetLink(String destination, String verificationCode) {
        return OperationResult.success();
    }
}
