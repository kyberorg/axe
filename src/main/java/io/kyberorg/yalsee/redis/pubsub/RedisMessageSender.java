package io.kyberorg.yalsee.redis.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisMessageSender {

    private final RedisTemplate<String, YalseeMessage> redisTemplate;
    private final ChannelTopic topic;

    public void sendMessage(YalseeMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
