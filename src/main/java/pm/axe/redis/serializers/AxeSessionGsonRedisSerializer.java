package pm.axe.redis.serializers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import pm.axe.internal.AxeGsonExclusionStrategy;
import pm.axe.session.AxeSession;

/**
 * {@link RedisSerializer} for {@link AxeSession} objects, that uses {@link Gson}.
 *
 * @since 3.8
 */
public class AxeSessionGsonRedisSerializer implements RedisSerializer<AxeSession> {
    private final Gson gson;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";

    /**
     * Created {@link AxeSessionGsonRedisSerializer} with customised {@link Gson} instance.
     */
    public AxeSessionGsonRedisSerializer() {
        this.gson = new GsonBuilder()
                .addSerializationExclusionStrategy(AxeGsonExclusionStrategy.get())
                .setDateFormat(DATE_FORMAT).create();
    }

    @Override
    public byte[] serialize(final AxeSession o) throws SerializationException {
        if (o == null) return new byte[0];
        return gson.toJson(o).getBytes();
    }

    @Override
    public AxeSession deserialize(final byte[] bytes) throws SerializationException {
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, AxeSession.class);
    }
}
