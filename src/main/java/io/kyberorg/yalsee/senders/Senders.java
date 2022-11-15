package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.users.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Senders {
    private final NoOpTokenSender noOpTokenSender;
    private final MailTokenSender mailTokenSender;

    public TokenSender getSender(final AccountType accountType) {
        return switch (accountType) {
            case LOCAL -> noOpTokenSender;
            case EMAIL -> mailTokenSender;
        };
    }
}
