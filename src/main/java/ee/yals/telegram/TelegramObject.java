package ee.yals.telegram;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Objects;

import static ee.yals.constants.App.NO_VALUE;
import static ee.yals.telegram.TelegramArguments.EMPTY_ARGS;

/**
 * Contains message, username and other useful info from {@link org.telegram.telegrambots.api.objects.Update}
 *
 * @since 2.4
 */
public class TelegramObject {

    private TelegramArguments arguments = EMPTY_ARGS;
    private TelegramCommand command = TelegramCommand.UNKNOWN;
    private String username = NO_VALUE;

    private String userMessage;

    private TelegramObject(Update update) {
        this.parseUpdate(update);
        this.parseUserMessage();
    }

    public static TelegramObject createFromUpdate(Update update) {
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

    public TelegramCommand getCommand() {
        return command;
    }

    private void parseUpdate(Update update) {
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
        if (this.command == TelegramCommand.UNKNOWN) {
            //message already without any command
            this.arguments = createArgumentsFromMessageWithoutCommand(args[0]);
        } else if (this.command == TelegramCommand.YALS) {
            String cmd = args[0];
            String userMessageWithoutCommand = this.userMessage.replace(cmd, "").trim();
            if (StringUtils.isNotBlank(userMessageWithoutCommand)) {
                this.arguments = createArgumentsFromMessageWithoutCommand(userMessageWithoutCommand);
            } else {
                this.arguments = TelegramArguments.builder().buildEmpty();
            }
        } else {
            this.arguments = TelegramArguments.builder().buildEmpty();
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
}
