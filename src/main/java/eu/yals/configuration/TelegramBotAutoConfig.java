package eu.yals.configuration;

import eu.yals.constants.App;
import eu.yals.telegram.TelegramBot;
import eu.yals.utils.AppUtils;
import eu.yals.utils.UrlExtraValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Registering bot at start time
 *
 * @since 2.4
 */
@Configuration
@ConditionalOnBean(TelegramBot.class)
@Slf4j
public class TelegramBotAutoConfig {
    private static final String TAG = "[TelegramAutoConfig] ";

    private List<BotSession> sessions = new ArrayList<>();

    private final TelegramBot telegramBot;
    private final AppUtils appUtils;

    static {
        ApiContextInitializer.init();
    }

    public TelegramBotAutoConfig(TelegramBot telegramBot, AppUtils appUtils) {
        this.telegramBot = telegramBot;
        this.appUtils = appUtils;
    }

    @PostConstruct
    public void start() {
        if (appUtils.isTelegramDisabled()) {
            log.info("{} Telegram bot is disabled. Skipping configuration...", TAG);
            return;
        }

        log.info(TAG + "Registering telegram bot");

        boolean isBotAvailable = Objects.nonNull(telegramBot) && isServerUrlAvailable();
        String botStatus = isBotAvailable ? "available" : "not available";
        log.info(TAG + "Bot Status: " + botStatus);

        if (isBotAvailable) {
            TelegramBotsApi api = new TelegramBotsApi();
            log.debug(TAG + "Bot token: " + telegramBot.getBotToken());

            try {
                sessions.add(api.registerBot(telegramBot));
            } catch (TelegramApiRequestException e) {
                log.error(TAG + "Failed to register bot", e);
            }
        }
    }

    @PreDestroy
    public void stop() {
        sessions.stream().forEach(session -> {
            if (session != null) {
                session.stop();
            }
        });
    }

    private boolean isServerUrlAvailable() {
        String serverHostname = appUtils.getServerUrl();
        boolean isServerUrlPresentAndValid = UrlExtraValidator.isUrl(serverHostname);
        if (isServerUrlPresentAndValid) {
            return true;
        } else {
            log.error("{} Server URL is not valid or missing. Did '{}' property or ENV '{}' was set?",
                    TAG, App.Properties.SERVER_URL, App.Env.SERVER_URL);
            log.info("{} Server URL is {}", TAG, serverHostname);
            return false;
        }
    }
}
