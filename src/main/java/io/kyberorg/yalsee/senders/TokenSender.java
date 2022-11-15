package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.TokenType;
import org.apache.commons.lang3.StringUtils;

public abstract class TokenSender {
    public abstract OperationResult send(Token token, String destination);

    protected boolean shouldCreateShortLink(final TokenType tt) {
        return StringUtils.isNotBlank(tt.getIdentPrefix());
    }
}
