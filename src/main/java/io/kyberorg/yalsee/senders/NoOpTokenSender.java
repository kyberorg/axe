package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Senders, that does nothing.
 */
@Slf4j
@Component
public class NoOpTokenSender extends TokenSender {
    private static final String TAG = "[" + NoOpTokenSender.class.getSimpleName() + "]";

    @Override
    public OperationResult send(final Token token, final String destination) {
        log.info("{} Sending {} to nowhere", TAG, Token.class.getSimpleName());
        return OperationResult.success();
    }
}
