package pm.axe.telegram.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.telegram.TelegramCommand;

/**
 * Interface for {@link TelegramCommand} implementation.
 */
public interface TelegramCommandHandler {
    /**
     * {@link TelegramCommand} implementation.
     *
     * @param update Telegram {@link Update} received.
     * @return string with message, which will be sent to user.
     */
    String handle(Update update);
}
