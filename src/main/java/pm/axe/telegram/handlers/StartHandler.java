package pm.axe.telegram.handlers;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.services.telegram.TelegramService;

@RequiredArgsConstructor
@Component
public class StartHandler implements TelegramCommandHandler {
    private final TelegramService telegramService;
    @Override
    public String handle(final Update update) {
        final String userName = update.getMessage().getFrom().getUserName();
        final String greetings = EmojiParser.parseToUnicode(String.format("%s Hello, %s!", Axe.Emoji.HELLO, userName));
        final String usage = telegramService.usage();
        return greetings + Axe.C.NEW_LINE + usage;
    }
}
