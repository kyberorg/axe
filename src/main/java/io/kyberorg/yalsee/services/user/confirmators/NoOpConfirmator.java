package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.result.OperationResult;
import org.springframework.stereotype.Component;

@Component
public class NoOpConfirmator implements Confirmator {
    @Override
    public OperationResult sendConfirmation(final String destination, final String confirmationToken) {
        return OperationResult.success();
    }
}
