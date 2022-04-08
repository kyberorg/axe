package io.kyberorg.yalsee.events.session;

import io.kyberorg.yalsee.session.YalseeSession;
import lombok.Data;

/**
 * Event indicates that {@link YalseeSession} created.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class YalseeSessionCreatedEvent {
    private final YalseeSession yalseeSession;
}
