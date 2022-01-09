package io.kyberorg.yalsee.redis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisMessageSubscriber implements MessageListener {
    public static final String TAG = "[" + RedisMessageSubscriber.class.getSimpleName() + "]";

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        log.info("{} Message received: {}", TAG, message.toString());
    }
}
