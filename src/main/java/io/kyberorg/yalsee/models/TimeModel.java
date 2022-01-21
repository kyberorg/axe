package io.kyberorg.yalsee.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Time-related extension for models. Provides {@link #created} and {@link #updated} fields.
 *
 * @since 3.4
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TimeModel extends BaseModel {
    @Column(name = "created", nullable = false)
    private Timestamp created = now();

    @Column(name = "updated", nullable = false)
    private Timestamp updated = now();

    /**
     * Provides current time aka now as {@link Timestamp}.
     *
     * @return {@link Timestamp} from {@link Instant#now()}
     */
    public static Timestamp now() {
        return Timestamp.from(Instant.now());
    }
}
