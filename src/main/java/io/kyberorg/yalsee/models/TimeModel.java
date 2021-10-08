package io.kyberorg.yalsee.models;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class TimeModel extends BaseModel {
    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "updated", nullable = false)
    private Timestamp updated;
}
