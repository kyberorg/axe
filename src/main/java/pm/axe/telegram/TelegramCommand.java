package pm.axe.telegram;

import org.apache.commons.lang3.StringUtils;

/**
 * List of supported by {@link TelegramBot} commands.
 *
 * @since 2.4
 */
public enum TelegramCommand {
    START("/start"),
    USAGE("/usage"),
    AXE("/axe"),
    HELLO("/hello"),
    NOT_A_COMMAND("__"),
    UNKNOWN("_");

    private final String commandString;

    TelegramCommand(final String cmd) {
        this.commandString = cmd;
    }

    /**
     * Get telegram command.
     *
     * @return string with command string
     */
    public String getCommandText() {
        return commandString;
    }

    /**
     * Constructs {@link TelegramCommand} with string.
     *
     * @param cmd string with command
     * @return created object
     */
    public static TelegramCommand createFromString(final String cmd) {
        if (StringUtils.isBlank(cmd)) {
            return UNKNOWN;
        }

        if (!cmd.startsWith("/")) {
            return NOT_A_COMMAND;
        }

        if (cmd.equals(START.commandString)) {
            return START;
        } else if (cmd.equals(AXE.commandString)) {
            return AXE;
        } else if (cmd.equals(USAGE.commandString)) {
            return USAGE;
        } else {
            return UNKNOWN;
        }
    }

    /**
     * Validates command.
     *
     * @return true - if match found, false if not
     */
    public boolean isAxeCommand() {
        return (this == AXE);
    }

}
