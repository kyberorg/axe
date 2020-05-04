package eu.yals.utils.push;

import com.vaadin.flow.component.Component;
import lombok.Getter;

/**
 * Object for keeping field to construct push message from.
 *
 * @since 2.7
 */
public final class Push {
    private static final String MARKER = "PUSH";
    private static final String NULL = "null";

    private static final String UI_PACKAGE = "eu.yals.ui";

    @Getter
    private final PushCommand pushCommand;

    @Getter
    private final Class<? extends Component> destination;

    /**
     * Starts {@link Push} object Construction with given {@link PushCommand}.
     *
     * @param pushCommand valid non null {@link PushCommand}
     * @return {@link PushBuilder} to continue building {@link Push} object
     */
    public static PushBuilder command(PushCommand pushCommand) {
        return new PushBuilder(pushCommand);
    }

    /**
     * Parses push message to {@link Push} object.
     *
     * @param message string with push message (normally constructed by {@link #toString()}
     * @return if parsing succeeds - valid {@link Push} object, if fails - {@link Push} with empty fields
     */
    public static Push fromMessage(final String message) {
        if (message.startsWith(MARKER)) {
            String[] parts = message.split("-");
            int PARTS_IN_VALID_ARRAY = 3;
            if (parts.length == PARTS_IN_VALID_ARRAY) {
                String commandString = parts[2];
                String destinationString = parts[1];

                PushCommand pushCommand = PushCommand.valueOf(commandString);
                Class<? extends Component> clazz;

                try {
                    //noinspection unchecked
                    clazz = (Class<? extends Component>) Class.forName(UI_PACKAGE + "." + destinationString);
                } catch (ClassNotFoundException | ClassCastException e) {
                    clazz = null;
                }
                return new Push(pushCommand, clazz);
            } else {
                return createNotValidObject();
            }
        } else {
            return createNotValidObject();
        }
    }

    private Push(final PushCommand pushCommand, final Class<? extends Component> component) {
        this.pushCommand = pushCommand;
        this.destination = component;
    }

    /**
     * Creates string which can be used as push message. This string contains command and string with UI class name.
     *
     * @return string with push message like PUSH-HomeView-COMMAND
     */
    @Override
    public String toString() {
        String target = (destination != null) ? destination.getSimpleName() : NULL;
        String command = (pushCommand != null) ? pushCommand.toString() : NULL;
        return String.format("%s-%s-%s", MARKER, target, command);
    }

    /**
     * Validates {@link Push} object.
     *
     * @return true if {@link Push} object has all field correctly filled, false if not.
     */
    public boolean valid() {
        return (pushCommand != null && destination != null);
    }

    private static Push createNotValidObject() {
        return new Push(null, null);
    }

    /**
     * Interim object to hide {@link Push} object until it fully constructed.
     */
    public static class PushBuilder {
        private final PushCommand pushCommand;

        private PushBuilder(final PushCommand pushCommand) {
            this.pushCommand = pushCommand;
        }

        /**
         * Adds push notification destination UI.
         *
         * @param view class in UI package for which this push message for.
         * @return fully constructed {@link Push} object
         */
        public Push dest(final Class<? extends Component> view) {
            return new Push(pushCommand, view);
        }
    }
}
