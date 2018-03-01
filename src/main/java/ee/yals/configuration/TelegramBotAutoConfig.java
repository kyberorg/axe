package ee.yals.configuration;

import ee.yals.telegram.TelegramBot;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;
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

    private List<BotSession> sessions = new ArrayList<>();

    @Autowired
    private TelegramBot telegramBot;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void start() {
        LOG.info("Registering telegram bot");

        boolean isBotAvailable = Objects.nonNull(telegramBot);
        String botStatus = isBotAvailable ? "available" : "NULL";
        LOG.info("Bot Status: " + botStatus);

        if (isBotAvailable) {
            TelegramBotsApi api = new TelegramBotsApi();
            LOG.info("Bot token: " + telegramBot.getBotToken());

            try {
                sessions.add(api.registerBot(telegramBot));
            } catch (TelegramApiRequestException e) {
                LOG.error("Failed to register bot", e);
            }
        }
    }

    public void stop() {
        sessions.stream().forEach(session -> {
            if (session != null) {
                session.stop();
            }
        });
    }
}
