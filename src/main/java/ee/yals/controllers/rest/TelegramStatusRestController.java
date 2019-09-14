package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.ErrorJson;
import ee.yals.json.TelegramStatusResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.telegram.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Reports telegram bot status
 *
 * @since 1.0
 */
@RestController
@Slf4j
public class TelegramStatusRestController {
    private static final String TAG = "[API Telegram Status]";
    private static final String ONLINE = "Online";
    private static final String OFFLINE = "Offline";

    private TelegramBot bot;

    public TelegramStatusRestController(TelegramBot bot) {
        this.bot = bot;
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.TELEGRAM_STATUS_API)
    public Json getBotStatus() {
        log.info("{} got request", TAG);
        if (bot == null) {
            //most likely you want want see it as application startup will fail
            log.error("Failed to autowire " + TelegramBot.class.getSimpleName());
            return ErrorJson.createWithMessage("Internal error: bot is missing");
        }

        try {
            String botName = bot.getMe().getUserName();
            String botStatus = ONLINE;
            log.info("{} telegram bot {} is {}", TAG, botName, botStatus);
            return TelegramStatusResponseJson.createWithStatus(botStatus).withBotName(botName);
        } catch (TelegramApiException e) {
            return TelegramStatusResponseJson.createWithStatus(OFFLINE).withBotName("-");
        }
    }
}
