package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.LinkInfo;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class LinkInfoUpdatedEvent {
    private final LinkInfo linkInfo;
}
