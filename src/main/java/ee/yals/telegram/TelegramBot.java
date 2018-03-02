package ee.yals.telegram;

import ee.yals.models.Link;
import ee.yals.services.telegram.TelegramService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ee.yals.services.telegram.TelegramServiceImpl.NO_INIT;
import static ee.yals.telegram.TelegramArguments.BROKEN_ARGS;
import static ee.yals.telegram.TelegramArguments.EMPTY_ARGS;

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
    private static final Message NO_MESSAGE = new Message();

    @Autowired
    private TelegramService telegramService;

    private Update update;
    private TelegramObject telegramObject;

    @Override
    public void onUpdateReceived(Update update) {
        LOG.debug(TAG + "New Update " + update);
        this.update = update;

        String message;
        try {
            if (Objects.isNull(telegramService)) {
                throw new IllegalStateException("Internal server error: ");
            }

            telegramObject = TelegramObject.createFromUpdate(update);
            telegramService.init(telegramObject);

            TelegramCommand telegramCommand = telegramObject.getCommand();
            switch (telegramCommand) {
                case YALS:
                case NOT_A_COMMAND:
                    message = doYals();
                    break;
                case START:
                case USAGE:
                case UNKNOWN:
                    message = telegramService.usage();
                    break;
                default:
                    message = telegramService.usage();
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
            execute(createSendMessage(message));
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

    private String doYals() {
        String message;
        if (telegramObject.getArguments() == EMPTY_ARGS) {
            throw new NoSuchElementException("Got empty command from Telegram. Nothing to shorten");
        } else if (telegramObject.getArguments() == BROKEN_ARGS) {
            throw new IllegalArgumentException("UserMessage must contain URL as first or second (when first is command) param");
        }

        String url = telegramObject.getArguments().getUrl();

        Link savedLink = telegramService.storeLink(url);
        if (Objects.nonNull(savedLink)) {
            message = telegramService.success(savedLink);
        } else {
            message = telegramService.serverError();
        }
        return message;
    }

    private SendMessage createSendMessage(String message) {
        Message telegramMessage = getMessage();
        SendMessage sendMessage = new SendMessage().setChatId(telegramMessage.getChatId());
        sendMessage.setText(message);
        return sendMessage;
    }

    private Message getMessage() {
        Message telegramMessage;
        if (update.hasMessage()) {
            telegramMessage = update.getMessage();
        } else if (update.hasEditedMessage()) {
            telegramMessage = update.getEditedMessage();
        } else {
            telegramMessage = NO_MESSAGE;
        }
        return telegramMessage;
    }
}
