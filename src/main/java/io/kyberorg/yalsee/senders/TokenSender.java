package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.TokenType;
import org.apache.commons.lang3.StringUtils;

/**
 * Base for all Senders.
 */
public abstract class TokenSender {
    /**
     * Sending action.
     *
     * @param token       {@link Token} to send.
     * @param destination string with account name, {@link TokenSender} should sent to.
     * @return {@link OperationResult} with send result.
     */
    public abstract OperationResult send(Token token, String destination);

    /**
     * Defines if needed to create short link or not.
     *
     * @param tt {@link TokenType} to extract {@link TokenType#identPrefix}
     * @return true - if there is {@link TokenType#identPrefix} and therefore it needs to create short link,
     * false - is not.
     */
    protected boolean shouldCreateShortLink(final TokenType tt) {
        return StringUtils.isNotBlank(tt.getIdentPrefix());
    }
}
