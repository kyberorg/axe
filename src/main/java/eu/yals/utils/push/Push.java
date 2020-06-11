package eu.yals.utils.push;

import lombok.Getter;

/**
 * Object for keeping field to construct push message from.
 *
 * @since 2.7
 */
public final class Push {
    private static final String PUSH_MESSAGE_MARKER = "PUSH";
    private static final int PARTS_IN_VALID_ARRAY = 2;

    @Getter
    private final PushCommand pushCommand;

    /**
     * Constructs {@link Push} object with given {@link PushCommand}.
     *
     * @param pushCommand valid non null {@link PushCommand}
     * @return {@link Push} object
     */
    public static Push command(final PushCommand pushCommand) {
        return new Push(pushCommand);
    }

    /**
     * Parses push message to {@link Push} object.
     *
     * @param message string with push message (normally constructed by {@link #toString()}
     * @return if parsing succeeds - valid {@link Push} object, if fails - {@link Push} with empty fields
     */
    public static Push fromMessage(final String message) {
        if (message.startsWith(PUSH_MESSAGE_MARKER)) {
            String[] parts = message.split("-");
            if (parts.length == PARTS_IN_VALID_ARRAY) {
                String commandString = parts[1];

                PushCommand pushCommand = PushCommand.valueOf(commandString);

                return new Push(pushCommand);
            } else {
                return createNotValidObject();
            }
        } else {
            return createNotValidObject();
        }
    }

    private Push(final PushCommand pushCommand) {
        this.pushCommand = pushCommand;
    }

    /**
     * Creates string which can be used as push message. This string contains command.
     *
     * @return string with push message like PUSH-COMMAND
     */
    @Override
    public String toString() {
        String command = (pushCommand != null) ? pushCommand.toString() : null;
        return String.format("%s-%s", PUSH_MESSAGE_MARKER, command);
    }

    /**
     * Validates {@link Push} object.
     *
     * @return true if {@link Push} object has all field correctly filled, false if not.
     */
    public boolean valid() {
        return (pushCommand != null);
    }

    private static Push createNotValidObject() {
        return new Push(null);
    }
}
