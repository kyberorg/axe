package io.kyberorg.yalsee.redis.pubsub;

public interface MessageSender {
    void sendMessage(String message);
}
