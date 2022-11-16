package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;

/**
 * Base for all Senders.
 */
public abstract class TokenSender {
    /**
     * Sending action.
     *
     * @param token       {@link Token} to send.
     * @param destination string with account name, {@link TokenSender} should send to.
     * @return {@link OperationResult} with send result.
     */
    public abstract OperationResult send(Token token, String destination);

}
