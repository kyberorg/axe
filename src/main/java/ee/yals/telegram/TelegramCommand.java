package ee.yals.telegram;

import org.apache.commons.lang3.StringUtils;

/**
 * List of supported by {@link TelegramBot} commands
 *
 * @since 2.4
 */
public enum TelegramCommand {
    START("/start"),
    USAGE("/usage"),
    YALS("/yals"),
    YALST("/yalst"),
    YALSL("/yalsl"),
    NOT_A_COMMAND("__"),
    UNKNOWN("_");

    public String getCommandText() {
        return commandString;
    }

    private String commandString;

    TelegramCommand(String cmd) {
        this.commandString = cmd;
    }

    public static TelegramCommand createFromString(String cmd) {
        if (StringUtils.isBlank(cmd)) {
            return UNKNOWN;
        }

        if (!cmd.startsWith("/")) {
            return NOT_A_COMMAND;
        }

        boolean isYalsCommand = cmd.equals(YALS.commandString) || cmd.equals(YALST.commandString) || cmd.equals(YALSL.commandString);

        if (cmd.equals(START.commandString)) {
            return START;
        } else if (isYalsCommand) {
            return YALS;
        } else if (cmd.equals(USAGE.commandString)) {
            return USAGE;
        } else {
            return UNKNOWN;
        }
    }

}
