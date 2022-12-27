package pm.axe.telegram.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.db.models.Link;
import pm.axe.db.models.User;
import pm.axe.internal.LinkServiceInput;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.services.telegram.TelegramService;
import pm.axe.telegram.TelegramArguments;
import pm.axe.telegram.TelegramObject;
import pm.axe.telegram.TelegramUserMapping;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultHandler implements TelegramCommandHandler {
    private static final String TAG = "[" + DefaultHandler.class.getSimpleName() + "]";
    private final TelegramService telegramService;
    private final LinkService linkService;
    private final TelegramUserMapping userMapping;

    @Override
    public String handle(final Update update) {
        String message;
        final TelegramObject telegramObject = TelegramObject.createFromUpdate(update);
        if (log.isDebugEnabled()) {
            log.debug(TAG + " Debugging " + TelegramObject.class.getSimpleName() + Axe.C.NEW_LINE + telegramObject);
        }

        if (telegramObject.getArguments() == TelegramArguments.EMPTY_ARGS) {
            log.error("{} Got {} command without arguments. Nothing to shorten.", TAG, telegramObject.getCommand());
            return telegramService.usage();
        } else if (telegramObject.getArguments() == TelegramArguments.BROKEN_ARGS) {
            log.error("{} UserMessage must contain URL as first or second (when first is command) param.", TAG);
            return telegramService.usage();
        }

        String url = telegramObject.getArguments().getUrl();
        log.debug("{} URL received {}", TAG, url);

        LinkServiceInput.LinkServiceInputBuilder linkServiceInput = LinkServiceInput.builder(url);
        if (update.getMessage() != null && update.getMessage().getFrom() != null
                && StringUtils.isNotBlank(update.getMessage().getFrom().getUserName())) {
            String tgUser = update.getMessage().getFrom().getUserName();
            Optional<User> linkOwner = userMapping.getAxeUser(tgUser);
            linkOwner.ifPresent(linkServiceInput::linkOwner);
        }

        OperationResult storeResult = linkService.createLink(linkServiceInput.build());
        if (storeResult.ok()) {
            message = telegramService.success(telegramObject.getUsername(),
                    storeResult.getPayload(Link.class),
                    telegramObject.getArguments().getDescription());
        } else {
            message = telegramService.serverError();
        }
        return message;
    }
}
