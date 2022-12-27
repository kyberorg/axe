package pm.axe.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pm.axe.services.telegram.TelegramService;
import pm.axe.telegram.handlers.*;
import pm.axe.utils.AppUtils;

import java.util.Optional;

/**
 * Telegram Bot.
 *
 * @since 2.4
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String TAG = "[" + TelegramBot.class.getSimpleName() + "]";

    private final TelegramService telegramService;
    private final AppUtils appUtils;

    private final DefaultHandler defaultHandler;
    private final HelloHandler helloHandler;
    private final MyAxeUserHandler myAxeUserHandler;
    private final ShowUsageHandler showUsageHandler;
    private final StartHandler startHandler;
    private final UnlinkHandler unlinkHandler;

    @Override
    public void onUpdateReceived(final Update update) {
        if (log.isTraceEnabled()) {
            log.trace(TAG + " New Update " + update);
        }
        String message;
        Optional<Message> telegramMessage = telegramService.getTelegramMessage(update);
        long chatId = telegramService.getChatId(update);
        if (telegramMessage.isEmpty() || StringUtils.isBlank(telegramMessage.get().getText())) {
            message = telegramService.serverError();
            doSend(message, chatId);
            return;
        }

        log.debug("{} Update (Author: {}, RawMessage: {})",
                TAG, telegramMessage.get().getFrom().getUserName(), telegramMessage.get().getText());

        final TelegramCommand telegramCommand = telegramService.extractCommand(telegramMessage.get());

        TelegramCommandHandler handler = switch (telegramCommand) {
            case NOT_A_COMMAND -> defaultHandler;
            case START -> startHandler;
            case HELLO -> helloHandler;
            case UNLINK ->  unlinkHandler;
            case MY_AXE_USER -> myAxeUserHandler;
            default -> showUsageHandler;
        };
        message = handler.handle(update);
        doSend(message, chatId);
    }

    @Override
    public String getBotUsername() {
        return "Axe Bot";
    }

    @Override
    public String getBotToken() {
        return appUtils.getTelegramToken();
    }

    /**
     * Converts String Message to {@link SendMessage}.
     *
     * @param message string with message to send.
     * @param chatId chat id to send message to.
     * @return {@link SendMessage} with given message inside.
     */
    public SendMessage createSendMessage(final String message, final long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
    }

    private void doSend(final String message, long chatId) {
        if (chatId == TelegramService.WRONG_CHAT_ID) {
            log.error("{} Nowhere to send. Got Negative ChatId", TAG);
            return;
        }
        try {
            execute(createSendMessage(message, chatId));
        } catch (TelegramApiException e) {
            log.error("{} Failed to send telegram message. Message: {} {}: {}",
                    TAG, message, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
