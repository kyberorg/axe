package io.kyberorg.yalsee.internal;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kyberorg.yalsee.session.YalseeSession;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * {@link RedisSerializer} for {@link YalseeSession} objects, that uses {@link Gson}.
 *
 * @since 3.8
 */
public class YalseeSessionGsonRedisSerializer implements RedisSerializer<YalseeSession> {
    private final Gson gson;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";

    /**
     * Created {@link YalseeSessionGsonRedisSerializer} with customised {@link Gson} instance.
     */
    public YalseeSessionGsonRedisSerializer() {
        this.gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    }

    @Override
    public byte[] serialize(final YalseeSession o) throws SerializationException {
        if (o == null) return new byte[0];
        return gson.toJson(o).getBytes();
    }

    @Override
    public YalseeSession deserialize(final byte[] bytes) throws SerializationException {
        String jsonString = new String(bytes);
        YalseeSession session = gson.fromJson(jsonString, YalseeSession.class);
        session.fixObjectLinksAfterDeserialization();
        return session;
    }
}
