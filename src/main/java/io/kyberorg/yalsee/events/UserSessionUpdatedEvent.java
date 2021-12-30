package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.redis.UserSession;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class UserSessionUpdatedEvent {
    private final UserSession session;
}
