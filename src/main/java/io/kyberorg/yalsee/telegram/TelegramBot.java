package io.kyberorg.yalsee.telegram;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.telegram.TelegramService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Telegram Bot.
 *
 * @since 2.4
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String TAG = "[" + TelegramBot.class.getSimpleName() + "]";

    private static final Message NO_MESSAGE = new Message();

    private final TelegramService telegramService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    private Update update;
    private TelegramObject telegramObject;

    /**
     * Constructor for Spring autowiring.
     *
     * @param telegramService service for making responses
     * @param linkService service for acting with links
     * @param appUtils        application utils
     */
    public TelegramBot(final TelegramService telegramService, final LinkService linkService, final AppUtils appUtils) {
        this.telegramService = telegramService;
        this.linkService = linkService;
        this.appUtils = appUtils;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        log.trace(TAG + " New Update " + update);
        this.update = update;

        String message;
        try {
            if (Objects.isNull(telegramService)) {
                throw new IllegalStateException("Internal server error: ");
            }

            log.debug("{} Update (Author: {}, Message: {})", TAG,
                    getMessage().getFrom().getUserName(), getMessage().getText());

            telegramObject = TelegramObject.createFromUpdate(update);

            log.debug(TAG + " Debugging " + TelegramObject.class.getSimpleName() + App.NEW_LINE + telegramObject);
            telegramService.init(telegramObject);

            TelegramCommand telegramCommand = telegramObject.getCommand();
            if (telegramCommand.isYalseeCommand()) {
                message = doYalsee();
            } else {
                switch (telegramCommand) {
                    case NOT_A_COMMAND:
                        message = doYalsee();
                        break;
                    case START:
                    case USAGE:
                    case UNKNOWN:
                        //noinspection DuplicateBranchesInSwitch (in the name of readable code)
                        message = telegramService.usage();
                        break;
                    default:
                        message = telegramService.usage();
                        break;
                }
            }

        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error(TAG + " Got exception while handling incoming update."
                    + " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.usage();
        } catch (IllegalStateException e) {
            message = "Internal error: not all components are available. Bot currently not available";
            log.error(TAG + " " + message + " " + e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error(TAG + " Got unexpected exception while processing telegram update"
                    + " " + e.getClass().getName() + ": " + e.getMessage());
            message = telegramService.serverError();
        }

        if (message.equals(TelegramService.NO_INIT)) {
            log.error(TelegramService.NO_INIT);
            message = "Internal error: not all components are initialized";
        }

        try {
            execute(createSendMessage(message));
        } catch (TelegramApiException e) {
            log.error(TAG + " Failed to send telegram message. Message: " + message
                    + " " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "Yalsee Bot";
    }

    @Override
    public String getBotToken() {
        return appUtils.getTelegramToken();
    }

    private String doYalsee() {
        String message;
        if (telegramObject.getArguments() == TelegramArguments.EMPTY_ARGS) {
            throw new NoSuchElementException("Got " + telegramObject.getCommand()
                    + " command without arguments. Nothing to shorten");
        } else if (telegramObject.getArguments() == TelegramArguments.BROKEN_ARGS) {
            throw new IllegalArgumentException(
                    "UserMessage must contain URL as first or second (when first is command) param");
        }

        String url = telegramObject.getArguments().getUrl();


        OperationResult storeResult = linkService.createLink(url);
        if (storeResult.ok()) {
            message = telegramService.success(storeResult.getPayload(Link.class));
        } else {
            message = telegramService.serverError();
        }
        return message;
    }

    private SendMessage createSendMessage(final String message) {
        Message telegramMessage = getMessage();
        return SendMessage.builder()
                .chatId(Long.toString(telegramMessage.getChatId()))
                .text(message)
                .build();
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
