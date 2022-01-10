package io.kyberorg.yalsee.redis.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * Service that sends messages to Yalsee Application Channel in Redis aka Publisher.
 */
@RequiredArgsConstructor
@Service
public class RedisMessageSender {

    private final RedisTemplate<String, YalseeMessage> redisTemplate;
    private final ChannelTopic topic;

    /**
     * Sends {@link YalseeMessage} to our channel in Redis.
     *
     * @param message valid {@link YalseeMessage} to send.
     */
    public void sendMessage(final YalseeMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
