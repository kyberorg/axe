package pm.axe.events.session;

import lombok.Data;
import pm.axe.session.AxeSession;

/**
 * Event indicates that {@link AxeSession} expires within {@link AxeSession#TIMEOUT_FOR_WARNING_MINUTES}
 * and it is time to show warning.
 */
@Data(staticConstructor = "createWith")
public class AxeSessionAlmostExpiredEvent {
    private final AxeSession axeSession;
}
