package ee.yals.configuration;

import ee.yals.Env;
import ee.yals.telegram.TelegramBot;
import ee.yals.utils.AppUtils;
import ee.yals.utils.UrlExtraValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

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
public class TelegramBotAutoConfig {
    private static final Logger LOG = Logger.getLogger(TelegramBotAutoConfig.class);
    private static final String TAG = "[TelegramAutoConfig] ";

    private List<BotSession> sessions = new ArrayList<>();

    @Autowired
    private TelegramBot telegramBot;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void start() {
        LOG.info("");
        LOG.info(TAG + "Registering telegram bot");

        boolean isBotAvailable = Objects.nonNull(telegramBot) && isServerUrlAvailable();
        String botStatus = isBotAvailable ? "available" : "not available";
        LOG.info(TAG + "Bot Status: " + botStatus);

        if (isBotAvailable) {
            TelegramBotsApi api = new TelegramBotsApi();
            LOG.info(TAG + "Bot token: " + telegramBot.getBotToken());

            try {
                sessions.add(api.registerBot(telegramBot));
            } catch (TelegramApiRequestException e) {
                LOG.error(TAG + "Failed to register bot", e);
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
        String serverHostname = AppUtils.HostHelper.getServerUrl();
        boolean isServerUrlPresentAndValid = UrlExtraValidator.isUrl(serverHostname);
        if (isServerUrlPresentAndValid) {
            return true;
        } else {
            LOG.error(String.format("%s Server URL is not valid or missing. Did ENV '%s' was set?", TAG, Env.SERVER_URL));
            LOG.info(String.format("%s Server URL is %s", TAG, serverHostname));
            return false;
        }
    }
}
