package io.kyberorg.yalsee.internal;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kyberorg.yalsee.session.YalseeSession;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class YalseeSessionGsonRedisSerializer implements RedisSerializer<YalseeSession> {
    private final Gson gson;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";

    public YalseeSessionGsonRedisSerializer() {
        this.gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    }

    @Override
    public byte[] serialize(YalseeSession o) throws SerializationException {
        return gson.toJson(o).getBytes();
    }

    @Override
    public YalseeSession deserialize(byte[] bytes) throws SerializationException {
        String jsonString = new String(bytes);

        return gson.fromJson(jsonString, YalseeSession.class);
    }
}
