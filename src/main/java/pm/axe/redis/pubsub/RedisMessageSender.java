package pm.axe.redis.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * Service that sends messages to Axe Application Channel in Redis aka Publisher.
 */
@RequiredArgsConstructor
@Service
public class RedisMessageSender {

    private final RedisTemplate<String, AxeRedisMessage> redisTemplate;
    private final ChannelTopic topic;

    /**
     * Sends {@link AxeRedisMessage} to our channel in Redis.
     *
     * @param message valid {@link AxeRedisMessage} to send.
     */
    public void sendMessage(final AxeRedisMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
