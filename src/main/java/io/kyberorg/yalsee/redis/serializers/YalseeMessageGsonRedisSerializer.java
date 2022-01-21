package io.kyberorg.yalsee.redis.serializers;


import com.google.gson.Gson;
import io.kyberorg.yalsee.redis.pubsub.YalseeMessage;
import io.kyberorg.yalsee.utils.AppUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * {@link RedisSerializer} for {@link YalseeMessage} objects, that uses {@link Gson}.
 *
 * @since 3.8
 */
public class YalseeMessageGsonRedisSerializer implements RedisSerializer<YalseeMessage> {

    @Override
    public byte[] serialize(final YalseeMessage o) throws SerializationException {
        if (o == null) return new byte[0];
        return AppUtils.GSON.toJson(o).getBytes();
    }

    @Override
    public YalseeMessage deserialize(final byte[] bytes) throws SerializationException {
        String jsonString = new String(bytes);
        return AppUtils.GSON.fromJson(jsonString, YalseeMessage.class);
    }
}
