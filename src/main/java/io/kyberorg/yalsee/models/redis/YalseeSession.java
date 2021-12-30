package io.kyberorg.yalsee.models.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.internal.Primitives;
import io.kyberorg.yalsee.events.YalseeSessionUpdatedEvent;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Application Session object.
 *
 * @since 3.8
 */
@Data
@NoArgsConstructor
public class YalseeSession implements Serializable {
    /**
     * Default length of session id.
     */
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

    /**
     * Retrieves value stored in session by its key. Value is converted to provided class.
     *
     * @param key          non-empty string with key to search for.
     * @param classOfValue class of value object
     * @param <T>          generic param
     * @return value converted to given class or {@code null}, if nothing found or value if null.
     * @throws IllegalArgumentException if key or classOfValue is null.
     * @throws ClassCastException       if value cannot be converted to requested class.
     */
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

    /**
     * Sets value into session under given key. If key exists - overwrites the value.
     * Also fires {@link YalseeSessionUpdatedEvent}.
     *
     * @param key   non-empty string with key to store under.
     * @param value any object as value.
     * @throws IllegalArgumentException if key is {@code null}.
     */
    public void setValue(final String key, final Object value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        this.values.put(key, value);
        EventBus.getDefault().post(YalseeSessionUpdatedEvent.createWith(this));
    }

    public boolean expired() {
        return notValidAfter.before(Date.from(Instant.now()));
    }
}
