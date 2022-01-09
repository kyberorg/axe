package io.kyberorg.yalsee.redis.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisMessageSender implements MessageSender {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void sendMessage(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
