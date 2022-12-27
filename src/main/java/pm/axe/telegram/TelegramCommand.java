package pm.axe.telegram;

import org.apache.commons.lang3.StringUtils;

/**
 * List of supported by {@link TelegramBot} commands.
 *
 * @since 2.4
 */
public enum TelegramCommand {
    START("/start"),
    HELLO("/hello"),
    USAGE("/usage"),
    AXE("/axe"),
    MY_AXE_USER("/myAxeUser"),
    UNLINK("/unlink"),
    NOT_A_COMMAND("__"),
    UNKNOWN("_");

    private final String cmdString;

    TelegramCommand(final String cmd) {
        this.cmdString = cmd;
    }

    /**
     * Get telegram command.
     *
     * @return string with command string
     */
    public String getCommandText() {
        return cmdString;
    }

    /**
     * Constructs {@link TelegramCommand} with string.
     *
     * @param cmd string with command
     * @return created object
     */
    public static TelegramCommand createFromString(final String cmd) {
        if (StringUtils.isBlank(cmd) || !cmd.startsWith("/")) {
            return NOT_A_COMMAND;
        }

        for (TelegramCommand tgCmd : TelegramCommand.values()) {
            if (cmd.equals(tgCmd.getCommandText())) {
                return tgCmd;
            }
        }

        return TelegramCommand.UNKNOWN;
    }

}
