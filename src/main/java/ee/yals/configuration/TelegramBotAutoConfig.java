package ee.yals.configuration;

import ee.yals.App;
import ee.yals.telegram.TelegramBot;
import ee.yals.utils.AppUtils;
import ee.yals.utils.UrlExtraValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TelegramBot telegramBot;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void start() {
        log.info("");
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
        String serverHostname = AppUtils.getServerUrl();
        boolean isServerUrlPresentAndValid = UrlExtraValidator.isUrl(serverHostname);
        if (isServerUrlPresentAndValid) {
            return true;
        } else {
            log.error(String.format("%s Server URL is not valid or missing. Did '%s' property or ENV '%s' was set?",
                    TAG, App.Properties.SERVER_URL, App.Env.SERVER_URL));
            log.info(String.format("%s Server URL is %s", TAG, serverHostname));
            return false;
        }
    }
}
