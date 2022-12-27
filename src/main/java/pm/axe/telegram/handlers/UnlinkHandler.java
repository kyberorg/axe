package pm.axe.telegram.handlers;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.db.models.Account;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.telegram.TelegramUserMapping;
import pm.axe.users.AccountType;
import pm.axe.utils.AppUtils;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnlinkHandler implements TelegramCommandHandler {
    private static final String TAG = "[" + UnlinkHandler.class.getSimpleName() + "]";
    private final AccountService accountService;
    private final TelegramUserMapping userMapping;
    private final AppUtils appUtils;

    @Override
    public String handle(final Update update) {
        //get message from update
        final Message message = update.getMessage();

        final String tgUser = message.getFrom().getUserName();
        if (userMapping.hasMapping(tgUser)) {
            deleteAccount(tgUser);
            deleteMapping(tgUser);

            return EmojiParser.parseToUnicode(
                    String.format("%s Done! Telegram account is no longer linked to any %s user",
                    Axe.Emoji.SUCCESS, StringUtils.capitalize(appUtils.getServerDomain().toLowerCase()))
            );
        } else {
            return EmojiParser.parseToUnicode(Axe.Emoji.WARNING + " Cannot unlink something, that is not linked yet.");
        }
    }

    @Async
    public void deleteAccount(final String tgUser) {
        Optional<Account> account = accountService.getAccountByAccountName(tgUser, AccountType.TELEGRAM);
        if (account.isEmpty()) {
            log.warn("{} failed to delete telegram {} for {}. Reason: {} is not found.",
                    TAG, Account.class.getSimpleName(), tgUser, Account.class.getSimpleName());
            return;
        }
        OperationResult opResult = accountService.deleteAccount(account.get());
        if(opResult.notOk()) {
            log.error("{} failed to delete {}. OpResult: {}", TAG, Account.class.getSimpleName(), opResult);
        }
    }

    @Async
    public void deleteMapping(final String tgUser) {
        userMapping.deleteMapping(tgUser);
    }
}
