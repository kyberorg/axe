package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.session.YalseeSession;
import lombok.Data;

/**
 * Event indicates that {@link YalseeSession} values where updated, and it is time to sync with storage.
 *
 * @since 3.8
 */
@Data
public class YalseeSessionUpdatedEvent {
    private final YalseeSession yalseeSession;

    public static YalseeSessionUpdatedEvent createWith(final YalseeSession yalseeSession) {
        YalseeSessionUpdatedEvent event = new YalseeSessionUpdatedEvent(yalseeSession);
        event.yalseeSession.updateVersion();
        return event;
    }
}
