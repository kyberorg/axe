package eu.yals.services.telegram;

import eu.yals.models.Link;
import eu.yals.telegram.TelegramBot;
import eu.yals.telegram.TelegramObject;

/**
 * Service for {@link TelegramBot}
 *
 * @since 2.4
 */
public interface TelegramService {

    void init(TelegramObject telegramObject);

    Link storeLink(String longUrl);

    String success(Link savedLink);

    String usage();

    String serverError();

}
