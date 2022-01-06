package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.session.YalseeSession;
import lombok.Data;

/**
 * Event indicates that {@link YalseeSession} destroyed aka ended and removed from session storage.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class YalseeSessionCreatedEvent {
    private final YalseeSession yalseeSession;
}
