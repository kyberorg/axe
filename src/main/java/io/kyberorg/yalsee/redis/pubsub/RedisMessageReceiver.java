package io.kyberorg.yalsee.redis.pubsub;

import io.kyberorg.yalsee.redis.serializers.YalseeMessageGsonRedisSerializer;
import io.kyberorg.yalsee.services.YalseeSessionService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

/**
 * Receives messages from Yalsee Redis Channel.
 * It does deserialization with {@link YalseeMessageGsonRedisSerializer#deserialize(byte[])},
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

        //convert raw body to YalseeMessage: we have to do it manually.
        YalseeMessage yalseeMessage;
        try {
            yalseeMessage = new YalseeMessageGsonRedisSerializer().deserialize(message.getBody());
        } catch (SerializationException e) {
            log.warn("{} Failed to deserialize received {}. Reason: {}", TAG, YalseeMessage.class.getSimpleName(), e);
            return;
        }
        //check if converted object has all needed fields.
        if (yalseeMessage == null || yalseeMessage.getEvent() == null
                || StringUtils.isBlank(yalseeMessage.getPayload())) {
            log.warn("{} Got malformed {}. Message: {}", TAG, YalseeMessage.class.getSimpleName(), yalseeMessage);
            return;
        }

        //filter out messages sent from same instance
        if (yalseeMessage.getActor().equals(AppUtils.getHostname())) {
            log.debug("{} This is our message - ignoring...", TAG);
            return;
        }

        //routing by event
        final YalseeSessionService sessionService = YalseeSessionService.getInstance();
        switch (yalseeMessage.getEvent()) {
            case YALSEE_SESSION_UPDATED:
                if (sessionService != null) sessionService.onRemoteUpdate(yalseeMessage.getPayload());
                break;
            case YALSEE_SESSION_DELETED:
                if (sessionService != null) sessionService.onRemoteDeletion(yalseeMessage.getPayload());
                break;
            default:
                log.debug("{} unknown event {}. Ignoring...", TAG, yalseeMessage.getEvent());
        }
    }
}
