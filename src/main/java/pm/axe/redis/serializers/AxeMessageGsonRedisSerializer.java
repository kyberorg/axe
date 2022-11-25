package pm.axe.redis.serializers;


import com.google.gson.Gson;
import pm.axe.redis.pubsub.AxeRedisMessage;
import pm.axe.utils.AppUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * {@link RedisSerializer} for {@link AxeRedisMessage} objects, that uses {@link Gson}.
 *
 * @since 3.8
 */
public class AxeMessageGsonRedisSerializer implements RedisSerializer<AxeRedisMessage> {

    @Override
    public byte[] serialize(final AxeRedisMessage o) throws SerializationException {
        if (o == null) return new byte[0];
        return AppUtils.GSON.toJson(o).getBytes();
    }

    @Override
    public AxeRedisMessage deserialize(final byte[] bytes) throws SerializationException {
        String jsonString = new String(bytes);
        return AppUtils.GSON.fromJson(jsonString, AxeRedisMessage.class);
    }
}
