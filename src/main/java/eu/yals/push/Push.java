package eu.yals.push;

import com.vaadin.flow.component.Component;
import lombok.Getter;

public class Push {
  private static final String MARKER = "PUSH";
  private static final String NULL = "null";

  private static final String UI_PACKAGE = "eu.yals.ui";

  @Getter
  private final PushCommand pushCommand;

  @Getter
  private final Class<? extends Component> destination;

  private Push(PushCommand pushCommand, Class<? extends Component> component) {
    this.pushCommand = pushCommand;
    this.destination = component;
    }

    public static PushBuilder command(PushCommand pushCommand) {
        return new PushBuilder(pushCommand);
    }

    public static Push fromMessage(String message) {
        if (message.startsWith(MARKER)) {
            String[] parts = message.split("-");
            if (parts.length == 3) {
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

    @Override
    public String toString() {
        String target = (destination != null) ? destination.getSimpleName() : NULL;
        String command = (pushCommand != null) ? pushCommand.toString() : NULL;
        return String.format("%s-%s-%s", MARKER, target, command);
    }

    public boolean valid() {
        return (pushCommand != null && destination != null);
    }

    private static Push createNotValidObject() {
        return new Push(null, null);
    }

    public static class PushBuilder {
        private final PushCommand pushCommand;

        public PushBuilder(PushCommand pushCommand) {
            this.pushCommand = pushCommand;
        }

        public Push dest(Class<? extends Component> component) {
            return new Push(pushCommand, component);
        }
    }
}
