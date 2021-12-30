package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.redis.YalseeSession;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class YalseeSessionUpdatedEvent {
    private final YalseeSession session;
}
