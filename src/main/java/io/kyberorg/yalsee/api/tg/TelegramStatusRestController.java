package io.kyberorg.yalsee.api.tg;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.json.TelegramStatusResponseJson;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.telegram.TelegramBot;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Reports telegram bot status.
 *
 * @since 2.5
 */
@Slf4j
@RestController
public class TelegramStatusRestController {
    private static final String TAG = "[" + TelegramStatusRestController.class.getSimpleName() + "]";
    private static final String ONLINE = "Online";
    private static final String OFFLINE = "Offline";

    private final TelegramBot bot;
    private final AppUtils appUtils;

    /**
     * Constructor for Spring autowiring.
     *
     * @param telegramBot      telegram bot
     * @param applicationUtils application utils to find out if telegram API is enabled
     */
    public TelegramStatusRestController(final TelegramBot telegramBot, final AppUtils applicationUtils) {
        this.bot = telegramBot;
        this.appUtils = applicationUtils;
    }

    /**
     * API Endpoint for getting telegram bot status.
     *
     * @return json with bot status
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.TELEGRAM_STATUS_API)
    public YalseeJson getBotStatus() {
        log.info("{} got request", TAG);
        if (bot == null) {
            //most likely you want want see it as application startup will fail
            log.error("{} Failed to autowire " + TelegramBot.class.getSimpleName(), TAG);
            return YalseeErrorJson.createWithMessage("Internal error: bot is missing");
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
