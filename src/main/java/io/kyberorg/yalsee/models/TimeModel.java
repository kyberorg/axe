package io.kyberorg.yalsee.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * Time-related extention for models. Provides {@link #created} and {@link #updated} fields.
 *
 * @since 3.4
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TimeModel extends BaseModel {
    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "updated", nullable = false)
    private Timestamp updated;
}
