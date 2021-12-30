package io.kyberorg.yalsee.models.dao.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Base DAO for Redis Hashes.
 *
 * @since 3.8
 */
@NoRepositoryBean
public class RedisDao {

    @Value("${spring.application.name}")
    private String appName;

    protected String getHashName(final Class<?> modelClass) {
        StringBuilder hashNameBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(appName)) {
            hashNameBuilder.append(appName);
            hashNameBuilder.append("-");
        }
        Optional<String> nameFromAnnotation = getHashNameFromAnnotation(modelClass);
        if (nameFromAnnotation.isPresent()) {
            hashNameBuilder.append(nameFromAnnotation.get());
        } else {
            hashNameBuilder.append(modelClass.getSimpleName());
        }
        return hashNameBuilder.toString();
    }

    protected long getTimeToLive(final Class<?> clazz) {
        long ttl = -1L;
        boolean hasRedisHashAnnotation = clazz.isAnnotationPresent(RedisHash.class);
        if (hasRedisHashAnnotation) {
            RedisHash redisHashAnno = clazz.getAnnotation(RedisHash.class);
            ttl = redisHashAnno.timeToLive();
        }
        return ttl;
    }

    private Optional<String> getHashNameFromAnnotation(final Class<?> clazz) {
        boolean hasRedisHashAnnotation = clazz.isAnnotationPresent(RedisHash.class);
        if (hasRedisHashAnnotation) {
            RedisHash redisHashAnno = clazz.getAnnotation(RedisHash.class);
            String redisHashName = redisHashAnno.value();
            return StringUtils.isNotBlank(redisHashName) ? Optional.of(redisHashName) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }
}
