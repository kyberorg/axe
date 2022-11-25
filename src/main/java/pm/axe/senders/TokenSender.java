package pm.axe.senders;

import pm.axe.db.models.Token;
import pm.axe.result.OperationResult;

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
