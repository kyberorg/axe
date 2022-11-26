package pm.axe.events.session;

import lombok.Data;
import pm.axe.session.AxeSession;

/**
 * Event indicates that {@link AxeSession} destroyed aka ended and removed from session storage.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class AxeSessionDestroyedEvent {
    private final AxeSession axeSession;
}
