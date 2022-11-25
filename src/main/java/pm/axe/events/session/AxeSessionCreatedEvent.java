package pm.axe.events.session;

import lombok.Data;
import pm.axe.session.AxeSession;

/**
 * Event indicates that {@link AxeSession} created.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class AxeSessionCreatedEvent {
    private final AxeSession axeSession;
}
