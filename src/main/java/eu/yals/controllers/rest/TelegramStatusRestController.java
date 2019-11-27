package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.json.ErrorJson;
import eu.yals.json.TelegramStatusResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.telegram.TelegramBot;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Reports telegram bot status
 *
 * @since 2.5
 */
@Slf4j
@RestController
public class TelegramStatusRestController {
    private static final String TAG = "[API Telegram Status]";
    private static final String ONLINE = "Online";
    private static final String OFFLINE = "Offline";

    private final TelegramBot bot;
    private final AppUtils appUtils;

    public TelegramStatusRestController(TelegramBot bot, AppUtils appUtils) {
        this.bot = bot;
        this.appUtils = appUtils;
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.TELEGRAM_STATUS_API)
    public Json getBotStatus() {
        log.info("{} got request", TAG);
        if (bot == null) {
            //most likely you want want see it as application startup will fail
            log.error("Failed to autowire " + TelegramBot.class.getSimpleName());
            return ErrorJson.createWithMessage("Internal error: bot is missing");
        }

        if (appUtils.isTelegramDisabled()) {
            log.info("{} Telegram Bot is disabled", TAG);
            return TelegramStatusResponseJson.createWithStatus(OFFLINE).withBotName("-");
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
