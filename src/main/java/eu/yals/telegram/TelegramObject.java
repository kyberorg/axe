package eu.yals.telegram;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static eu.yals.constants.App.NEW_LINE;
import static eu.yals.constants.App.NO_VALUE;

/**
 * Contains message, username and other useful info from {@link org.telegram.telegrambots.meta.api.objects.Update}.
 *
 * @since 2.4
 */
@Data
public final class TelegramObject {

    private TelegramArguments arguments = TelegramArguments.EMPTY_ARGS;
    private TelegramCommand command = TelegramCommand.UNKNOWN;
    private String username = NO_VALUE;

    private String userMessage;

    private TelegramObject(final Update update) {
        this.parseUpdate(update);
        this.parseUserMessage();
    }

    /**
     * Creates {@link TelegramObject} for telegram {@link Update}.
     *
     * @param update telegram update object
     * @return created {@link TelegramObject}
     */
    public static TelegramObject createFromUpdate(final Update update) {
        if (Objects.isNull(update)) {
            throw new IllegalStateException("Update is missing");
        }

        return new TelegramObject(update);
    }

    private void parseUpdate(final Update update) {
        if (Objects.isNull(update)) {
            userMessage = NO_VALUE;
            return;
        }
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasEditedMessage()) {
            message = update.getEditedMessage();
        } else {
            userMessage = NO_VALUE;
            return;
        }

        username = message.getFrom().getUserName();
        userMessage = message.getText();
    }

    private void parseUserMessage() {
        if (StringUtils.isBlank(this.userMessage) || this.userMessage.equals(NO_VALUE)) {
            this.arguments = TelegramArguments.builder().buildEmpty();
            return;
        }

        String[] args = this.userMessage.split(" ");
        if (args.length == 0) {
            this.arguments = TelegramArguments.builder().buildEmpty();
            return;
        }

        this.command = TelegramCommand.createFromString(args[0]);

        switch (this.command) {
            case NOT_A_COMMAND:
                this.arguments = createArgumentsFromMessageWithoutCommand(this.userMessage);
                break;
            case YALS:
            case YALST:
            case YALSL:
                String remainedArgs = this.userMessage.replace(this.command.getCommandText(), "").trim();
                this.arguments = createArgumentsFromMessageWithoutCommand(remainedArgs);
                break;
            case UNKNOWN:
            case USAGE:
            case START:
            default:
                //no reason for manipulating with user message
                this.arguments = TelegramArguments.builder().buildEmpty();
                break;
        }
    }

    private TelegramArguments createArgumentsFromMessageWithoutCommand(final String userMessageWithoutCommand) {
        TelegramArguments arguments;
        String[] args = userMessageWithoutCommand.split(" ");
        String url = "";
        String description = "";
        if (args.length > 0) {
            url = args[0];
        }
        if (args.length > 1) {
            description = userMessageWithoutCommand.replace(url, "").trim();
        }

        if (StringUtils.isBlank(url)) {
            arguments = TelegramArguments.EMPTY_ARGS;
        } else {
            if (StringUtils.isNotBlank(description)) {
                arguments = TelegramArguments.builderWithUrl(url).andDescription(description).build();
            } else {
                arguments = TelegramArguments.builderWithUrl(url).build();
            }
        }
        return arguments;
    }

    @Override
    public String toString() {
        return TelegramObject.class.getSimpleName() + " {" + NEW_LINE
                + TelegramArguments.class.getSimpleName() + "=" + arguments + NEW_LINE
                + TelegramCommand.class.getSimpleName() + "=" + command + NEW_LINE
                + "username=" + username + NEW_LINE
                + "userMessage=" + userMessage + NEW_LINE
                + "}";
    }
}
