package io.kyberorg.yalsee.redis.pubsub;

import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * Structure to communicate with in Yalsee Redis Channel.
 * Has needed meta info, event and optional {@link String payload}.
 *
 * @since 3.8
 */
@Data(staticConstructor = "create")
@ToString
public class YalseeMessage {
    @Getter
    private final String actor = AppUtils.getHostname();
    @Getter
    private final MessageEvent event;
    private String payload;
}
