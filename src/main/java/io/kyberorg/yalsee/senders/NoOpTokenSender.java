package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpTokenSender extends TokenSender {
    private static final String TAG = "[" + NoOpTokenSender.class.getSimpleName() + "]";

    @Override
    public OperationResult send(Token token, String destination) {
        log.info("{} Sending {} to nowhere", TAG, Token.class.getSimpleName());
        return OperationResult.success();
    }
}
