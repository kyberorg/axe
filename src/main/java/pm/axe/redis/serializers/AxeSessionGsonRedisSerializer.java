package pm.axe.redis.serializers;


import com.google.gson.Gson;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import pm.axe.session.AxeSession;
import pm.axe.utils.AppUtils;

/**
 * {@link RedisSerializer} for {@link AxeSession} objects, that uses {@link Gson}.
 *
 * @since 3.8
 */
public class AxeSessionGsonRedisSerializer implements RedisSerializer<AxeSession> {
    private final Gson gson = AppUtils.GSON;

    @Override
    public byte[] serialize(final AxeSession o) throws SerializationException {
        if (o == null) return new byte[0];
        return gson.toJson(o).getBytes();
    }

    @Override
    public AxeSession deserialize(final byte[] bytes) throws SerializationException {
        String jsonString = bytes != null ? new String(bytes) : "{}";
        return gson.fromJson(jsonString, AxeSession.class);
    }
}
