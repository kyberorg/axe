package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.json.ErrorJson;
import ee.yals.json.TelegramStatusResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.telegram.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletResponse;

/**
 * Reports telegram bot status
 *
 * @since 1.0
 */
@RestController
@Slf4j
public class TelegramStatusRestController {
    private static final String TAG = "[API Telegram Status]";

    private TelegramBot bot;

    public TelegramStatusRestController(TelegramBot bot) {
        this.bot = bot;
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.TELEGRAM_STATUS_API)
    public Json getBotStatus(@RequestBody String body, HttpServletResponse response) {
        log.debug("{} got request: {}", TAG, body);
        if (bot == null) {
            //most likely you want want see it as application startup will fail
            log.error("Failed to autowire " + TelegramBot.class.getSimpleName());
            return ErrorJson.createWithMessage("Internal error: bot is missing");
        }

        try {
            String botName = bot.getMe().getUserName();
            String botStatus = bot.getMe().getBot() ? "Online" : "Offline";
            return TelegramStatusResponseJson.createWith(botName, botStatus);
        } catch (TelegramApiException e) {
            log.error("Got exception while ask info from bot", e);
            return ErrorJson.createWithMessage("Internal error: bot gave no info");
        }
    }
}
