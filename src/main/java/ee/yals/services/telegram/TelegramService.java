package ee.yals.services.telegram;

import ee.yals.models.Link;
import ee.yals.telegram.TelegramObject;

/**
 * Service for {@link ee.yals.telegram.TelegramBot}
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
