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

        if (cmd.equals(START.commandString)) {
            return START;
        } else if (cmd.equals(YALS.commandString)) {
            return YALS;
        } else if (cmd.equals(USAGE.commandString)) {
            return USAGE;
        } else {
            return UNKNOWN;
        }
    }

}
