package io.kyberorg.yalsee.events;

import io.kyberorg.yalsee.models.Link;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class LinkDeletedEvent {
    private final Link link;
}