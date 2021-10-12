package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.result.OperationResult;

public interface Confirmator {
    OperationResult sendConfirmation(String destination, String confirmationToken);
}
