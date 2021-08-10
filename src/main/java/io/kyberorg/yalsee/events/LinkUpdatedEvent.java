package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.Link;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class LinkUpdatedEvent {
    private final Link link;
}
