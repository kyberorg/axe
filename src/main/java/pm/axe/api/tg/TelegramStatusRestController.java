package pm.axe.api.tg;

import pm.axe.Endpoint;
import pm.axe.json.TelegramStatusResponse;
import pm.axe.json.AxeErrorJson;
import pm.axe.json.AxeJson;
import pm.axe.telegram.TelegramBot;
import pm.axe.utils.AppUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
public class TelegramStatusRestController {
    private static final String TAG = "[" + TelegramStatusRestController.class.getSimpleName() + "]";
    private static final String ONLINE = "Online";
    private static final String OFFLINE = "Offline";

    private final TelegramBot bot;
    private final AppUtils appUtils;

    /**
     * API Endpoint for getting telegram bot status.
     *
     * @return json with bot status
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.TELEGRAM_STATUS_API)
    public AxeJson getBotStatus() {
        log.info("{} got request", TAG);
        if (bot == null) {
            //most likely you want see it as application startup will fail
            log.error("{} Failed to autowire " + TelegramBot.class.getSimpleName(), TAG);
            return AxeErrorJson.createWithMessage("Internal error: bot is missing");
        }

        if (appUtils.isTelegramDisabled()) {
            log.info("{} Telegram Bot is disabled", TAG);
            return TelegramStatusResponse.createWithStatus(OFFLINE).withBotName("-disabled");
        }

        try {
            String botName = bot.getMe().getUserName();
            String botStatus = ONLINE;
            log.info("{} telegram bot {} is {}", TAG, botName, botStatus);
            return TelegramStatusResponse.createWithStatus(botStatus).withBotName(botName);
        } catch (TelegramApiException e) {
            log.warn("{} go Telegram exception {}", TAG, e.getMessage());
            log.warn("", e);
            return TelegramStatusResponse.createWithStatus(OFFLINE).withBotName("-broken");
        }
    }
}
