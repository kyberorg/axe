package io.kyberorg.yalsee.models.dao.base;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
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

    @Getter(AccessLevel.PROTECTED)
    private static String applicationPrefix = "Yalsee-";

    @Value("${redis.app.prefix}")
    private String applicationName;

    @PostConstruct
    private void init() {
        applicationPrefix = setApplicationPrefix();
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
    private String setApplicationPrefix() {
        String prefixValueSeparator = "-";
        if (StringUtils.isNotBlank(applicationName)) {
            return applicationName + prefixValueSeparator;
        } else {
            return applicationPrefix;
        }

    }
}
