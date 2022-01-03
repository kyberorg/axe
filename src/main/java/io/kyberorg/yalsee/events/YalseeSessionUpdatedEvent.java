package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.redis.YalseeSession;
import lombok.Data;

/**
 * Event indicates that {@link YalseeSession} values where updated, and it is time to sync with storage.
 *
 * @since 3.8
 */
@Data(staticConstructor = "createWith")
public class YalseeSessionUpdatedEvent {
    private final YalseeSession session;
}
