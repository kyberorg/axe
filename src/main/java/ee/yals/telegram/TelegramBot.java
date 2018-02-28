package ee.yals.telegram;

import ee.yals.models.Link;
import ee.yals.services.telegram.TelegramService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ee.yals.services.telegram.TelegramServiceImpl.NO_INIT;

/**
 * Telegram Bot
 *
 * @since 2.4
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger LOG = Logger.getLogger(TelegramBot.class);
    private static final String TAG = "[Telegram] ";

    private static final String DUMMY_TOKEN = "dummy:Token";

    @Autowired
    private TelegramService telegramService;

    private Update update;
    @Override
    public void onUpdateReceived(Update update) {
        LOG.debug(TAG + "New Update " + update);
        this.update = update;

        String message;
        try {
            if (Objects.isNull(telegramService)) {
                throw new IllegalStateException("Internal server error: ");
            }

            TelegramObject telegramObject = TelegramObject.createFromUpdate(update);
            telegramService.init(telegramObject);
            String url = telegramObject.getArguments().getUrl();

            Link savedLink = telegramService.storeLink(url);
            if (Objects.nonNull(savedLink)) {
                message = telegramService.success(savedLink);
            } else {
                message = telegramService.serverError();
            }

        } catch (NoSuchElementException | IllegalArgumentException e) {
            LOG.error(TAG + "Got exception while handling incoming update. Update: " + update, e);
            message = telegramService.usage();
        } catch (IllegalStateException e) {
            LOG.error(TAG + "Exception", e);
            message = "Internal error: not all components are available";
        } catch (Exception e) {
            LOG.error(TAG + " Got unexpected exception while processing telegram update. Update: " + update, e);
            message = telegramService.serverError();
        }

        if (message.equals(NO_INIT)) {
            LOG.error(NO_INIT);
            message = "Internal error: not all components are initialized";
        }

        try {
            sendMessage(createSendMessage(message));
        } catch (TelegramApiException e) {
            LOG.error(TAG + "Failed to send telegram message. Message: " + message, e);
        }
    }

    @Override
    public String getBotUsername() {
        return "Yals Bot";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv("TELEGRAM_TOKEN");
        return Objects.isNull(token) ? DUMMY_TOKEN : token;
    }

    private SendMessage createSendMessage(String message) {
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        sendMessage.setText(message);
        return sendMessage;
    }
}
