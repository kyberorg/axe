package io.kyberorg.yalsee.models.dao.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Base DAO for Redis Records.
 *
 * @since 3.8
 */
@NoRepositoryBean
public abstract class RedisDao {

    protected static String APPLICATION_PREFIX = "Yalsee-";

    @Value("${redis.app.prefix}")
    private String applicationPrefix;

    @PostConstruct
    private void init() {
        APPLICATION_PREFIX = appendApplicationPrefix();
    }

    /**
     * Sets record TTL aka Time-to-live aka expiration in {@link TimeUnit#SECONDS} after which record is deleted.
     *
     * @return number of {@link TimeUnit#SECONDS} record should live for.
     * Negative number means no TTL (Record won't expire).
     */
    protected abstract long getRecordTtl();

    /**
     * Provides application prefix and separator.
     * Needed to distinguish sessions of different applications stored within same DB.
     *
     * @return string with application prefix taken from properties and prefix-value separator. Example: "MyApp-"
     */
    protected String appendApplicationPrefix() {
        String prefixValueSeparator = "-";
        return applicationPrefix + prefixValueSeparator;
    }
}
