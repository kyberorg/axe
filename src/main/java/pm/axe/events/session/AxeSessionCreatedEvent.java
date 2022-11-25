package pm.axe.events.session;

import pm.axe.session.AxeSession;
import lombok.Data;

/**
 * Event indicates that {@link AxeSession} created.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class AxeSessionCreatedEvent {
    private final AxeSession axeSession;
}
