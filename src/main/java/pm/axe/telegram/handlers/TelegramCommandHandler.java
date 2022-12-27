package pm.axe.telegram.handlers;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe.Emoji;
import pm.axe.telegram.TelegramCommand;

/**
 * Base interface for {@link TelegramCommand} implementations.
 */
public interface TelegramCommandHandler {
    /**
     * {@link TelegramCommand} handler implementation.
     *
     * @param update Telegram {@link Update} received.
     * @return string with message, which will be sent to user.
     * If string contains {@link Emoji} wrap it with {@link EmojiParser#parseToUnicode(String)}
     */
    String handle(Update update);
}
