package eu.yals.telegram;

import eu.yals.constants.App;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static eu.yals.telegram.TelegramArguments.EMPTY_ARGS;

/**
 * Contains message, username and other useful info from {@link org.telegram.telegrambots.meta.api.objects.Update}
 *
 * @since 2.4
 */
public class TelegramObject {

    private TelegramArguments arguments = EMPTY_ARGS;
    private TelegramCommand command = TelegramCommand.UNKNOWN;
    private String username = App.NO_VALUE;

    private String userMessage;

    private TelegramObject(Update update) {
        this.parseUpdate(update);
        this.parseUserMessage();
    }

    static TelegramObject createFromUpdate(Update update) {
        if (Objects.isNull(update)) {
            throw new IllegalStateException("Update is missing");
        }

        return new TelegramObject(update);
    }

    public TelegramArguments getArguments() {
        return arguments;
    }

    public String getUsername() {
        return username;
    }

    TelegramCommand getCommand() {
        return command;
    }

    private void parseUpdate(Update update) {
        if (Objects.isNull(update)) {
            userMessage = App.NO_VALUE;
            return;
        }
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasEditedMessage()) {
            message = update.getEditedMessage();
        } else {
            userMessage = App.NO_VALUE;
            return;
        }

        username = message.getFrom().getUserName();
        userMessage = message.getText();
    }

    private void parseUserMessage() {
        if (StringUtils.isBlank(this.userMessage) || this.userMessage.equals(App.NO_VALUE)) {
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
                this.arguments = TelegramArguments.builder().buildEmpty(); //no reason for manipulating with user message
        }
    }

    private TelegramArguments createArgumentsFromMessageWithoutCommand(String userMessageWithoutCommand) {
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
        return TelegramObject.class.getSimpleName() + " {" + App.NEW_LINE +
                TelegramArguments.class.getSimpleName() + "=" + arguments + App.NEW_LINE +
                TelegramCommand.class.getSimpleName() + "=" + command + App.NEW_LINE +
                "username=" + username + App.NEW_LINE +
                "userMessage=" + userMessage + App.NEW_LINE +
                "}";
    }
}
