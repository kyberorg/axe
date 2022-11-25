package pm.axe.senders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pm.axe.db.models.Token;
import pm.axe.result.OperationResult;

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
