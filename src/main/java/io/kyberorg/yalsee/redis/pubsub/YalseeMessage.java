package io.kyberorg.yalsee.redis.pubsub;

import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data(staticConstructor = "create")
@ToString
public class YalseeMessage {
    @Getter
    private final String actor = AppUtils.getHostname();
    @Getter
    private final MessageEvent event;
    private String payload;
}
