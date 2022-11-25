package pm.axe.redis.pubsub;

import pm.axe.utils.AppUtils;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * Structure to communicate with in Axe Redis Channel.
 * Has needed meta info, event and optional {@link String payload}.
 *
 * @since 3.8
 */
@Data(staticConstructor = "create")
@ToString
public class AxeRedisMessage {
    @Getter
    private final String actor = AppUtils.getHostname();
    @Getter
    private final MessageEvent event;
    private String payload;
}
