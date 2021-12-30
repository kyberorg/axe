package io.kyberorg.yalsee.models.dao.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base DAO for Redis Records.
 *
 * @since 3.8
 */
@NoRepositoryBean
public abstract class RedisDao {
    private final String prefixValueSeparator = "-";

    @Value("${redis.app.prefix}")
    private String applicationPrefix;

    protected abstract long getRecordTtl();

    protected String appendApplicationPrefix() {
        return applicationPrefix + prefixValueSeparator;
    }

}
