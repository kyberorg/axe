package pm.axe.senders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pm.axe.users.AccountType;

/**
 * Senders factory.
 */
@RequiredArgsConstructor
@Component
public class Senders {
    private final NoOpTokenSender noOpTokenSender;
    private final MailTokenSender mailTokenSender;
    private final TelegramSender telegramSender;

    /**
     * Provides senders based on {@link AccountType}.
     *
     * @param accountType {@link AccountType} to determine {@link TokenSender} to return.
     * @return {@link TokenSender} actual to given {@link AccountType}.
     */
    public TokenSender getSender(final AccountType accountType) {
        return switch (accountType) {
            case LOCAL -> noOpTokenSender;
            case EMAIL -> mailTokenSender;
            case TELEGRAM -> telegramSender;
        };
    }
}
