package io.kyberorg.yalsee.events.session;

import io.kyberorg.yalsee.session.YalseeSession;
import lombok.Data;

/**
 * Event indicates that {@link YalseeSession} expires within {@link YalseeSession#TIMEOUT_FOR_WARNING_MINUTES}
 * and it is time to show warning.
 */
@Data(staticConstructor = "createWith")
public class YalseeSessionAlmostExpiredEvent {
    private final YalseeSession yalseeSession;
}
