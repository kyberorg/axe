package pm.axe.redis.pubsub;

import pm.axe.redis.serializers.AxeMessageGsonRedisSerializer;
import pm.axe.services.AxeSessionService;
import pm.axe.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

/**
 * Receives messages from Axe Redis Channel.
 * It does deserialization with {@link AxeMessageGsonRedisSerializer#deserialize(byte[])},
 * filtration and {@link MessageEvent}-based routing.
 *
 * @since 3.8
 */
@Slf4j
@Service
public class RedisMessageReceiver implements MessageListener {
    private static final String TAG = "[" + RedisMessageReceiver.class.getSimpleName() + "]";

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        log.debug("{} Message received: {}", TAG, message.toString());

        //convert raw body to AxeMessage: we have to do it manually.
        AxeRedisMessage axeRedisMessage;
        try {
            axeRedisMessage = new AxeMessageGsonRedisSerializer().deserialize(message.getBody());
        } catch (SerializationException e) {
            log.warn("{} Failed to deserialize received {}. Reason: {}", TAG, AxeRedisMessage.class.getSimpleName(), e);
            return;
        }
        //check if converted object has all needed fields.
        if (axeRedisMessage == null || axeRedisMessage.getEvent() == null
                || StringUtils.isBlank(axeRedisMessage.getPayload())) {
            log.warn("{} Got malformed {}. Message: {}", TAG, AxeRedisMessage.class.getSimpleName(), axeRedisMessage);
            return;
        }

        //filter out messages sent from same instance
        if (axeRedisMessage.getActor().equals(AppUtils.getHostname())) {
            log.debug("{} This is our message - ignoring...", TAG);
            return;
        }

        //routing by event
        final AxeSessionService sessionService = AxeSessionService.getInstance();
        switch (axeRedisMessage.getEvent()) {
            case AXE_SESSION_UPDATED:
                if (sessionService != null) sessionService.onRemoteUpdate(axeRedisMessage.getPayload());
                break;
            case AXE_SESSION_DELETED:
                if (sessionService != null) sessionService.onRemoteDeletion(axeRedisMessage.getPayload());
                break;
            default:
                log.debug("{} unknown event {}. Ignoring...", TAG, axeRedisMessage.getEvent());
        }
    }
}
