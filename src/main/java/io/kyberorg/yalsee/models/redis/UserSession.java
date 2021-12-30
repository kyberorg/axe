package io.kyberorg.yalsee.models.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.internal.Primitives;
import io.kyberorg.yalsee.events.UserSessionUpdatedEvent;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
//@RedisHash(value = "UserSession", timeToLive = App.Defaults.USER_SESSION_TTL_SECONDS)
@RedisHash(value = "UserSession", timeToLive = 5 * 60) //tmp to test
public class UserSession implements Serializable {
    public static final int SESSION_ID_LEN = 40;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";

    private String sessionId = RandomStringUtils.randomAlphanumeric(SESSION_ID_LEN);
    private Device device;
    private final Map<String, Object> values = new HashMap<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private final Date created = AppUtils.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date updated = AppUtils.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date notValidAfter = Date.from(Instant.now().plusSeconds(AppUtils.getSessionTimeoutFromStaticContext()));

    public <T> T getValue(final String key, final Class<T> classOfValue) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (classOfValue == null) {
            throw new IllegalArgumentException("Class cannot be null, use Object.class instead");
        }

        if (values.containsKey(key)) {
            Object value = values.get(key);
            if (value == null) {
                return null;
            } else {
                return Primitives.wrap(classOfValue).cast(value);
            }
        } else {
            return null;
        }
    }

    public void setValue(final String key, final Object payload) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        this.values.put(key, payload);
        EventBus.getDefault().post(UserSessionUpdatedEvent.createWith(this));
    }

    public boolean expired() {
        return notValidAfter.before(Date.from(Instant.now()));
    }
}
