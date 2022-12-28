package pm.axe.telegram.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.services.telegram.TelegramService;

@RequiredArgsConstructor
@Component
public class ShowUsageHandler implements TelegramCommandHandler {
    private final TelegramService telegramService;

    @Override
    public String handle(final Update update) {
       return telegramService.usage();
    }
}
