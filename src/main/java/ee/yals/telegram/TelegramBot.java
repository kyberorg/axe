package ee.yals.telegram;

import ee.yals.Env;
import ee.yals.models.Link;
import ee.yals.services.telegram.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ee.yals.constants.App.NEW_LINE;
import static ee.yals.services.telegram.TelegramServiceImpl.NO_INIT;
import static ee.yals.telegram.TelegramArguments.BROKEN_ARGS;
import static ee.yals.telegram.TelegramArguments.EMPTY_ARGS;

/**
 * Telegram Bot
 *
 * @since 2.4
 */
@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private static final String TAG = "[Telegram Bot]";

    public static final String DUMMY_TOKEN = "dummy:Token";
    private static final Message NO_MESSAGE = new Message();

    @Autowired
    private TelegramService telegramService;

    private Update update;
    private TelegramObject telegramObject;

    @Override
    public void onUpdateReceived(Update update) {
        log.trace(TAG + " New Update " + update);
        this.update = update;

        String message;
        try {
            if (Objects.isNull(telegramService)) {
                throw new IllegalStateException("Internal server error: ");
            }

            log.debug(String.format("%s Update (Author: %s, Message: %s)", TAG, getMessage().getFrom().getUserName(), getMessage().getText()));

            telegramObject = TelegramObject.createFromUpdate(update);

            log.debug(TAG + " Debugging " + TelegramObject.class.getSimpleName() + NEW_LINE + telegramObject);
            telegramService.init(telegramObject);

            TelegramCommand telegramCommand = telegramObject.getCommand();
            if (telegramCommand.isYalsCommand()) {
                message = doYals();
            } else {
                switch (telegramCommand) {
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
            }

        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error(TAG + " Got exception while handling incoming update." +
                    " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.usage();
        } catch (IllegalStateException e) {
            message = "Internal error: not all components are available. Bot currently not available";
            log.error(TAG + " " + message + " " + e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error(TAG + " Got unexpected exception while processing telegram update" +
                    " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.serverError();
        }

        if (message.equals(NO_INIT)) {
            log.error(NO_INIT);
            message = "Internal error: not all components are initialized";
        }

        try {
            execute(createSendMessage(message));
        } catch (TelegramApiException e) {
            log.error(TAG + " Failed to send telegram message. Message: " + message +
                    " " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "Yals Bot";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv(Env.TELEGRAM_TOKEN);
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }

    private String doYals() {
        String message;
        if (telegramObject.getArguments() == EMPTY_ARGS) {
            throw new NoSuchElementException("Got " + telegramObject.getCommand() + " command without arguments. Nothing to shorten");
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
