package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.dao.base.RedisDao;
import io.kyberorg.yalsee.models.redis.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * DAO for {@link UserSession}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class UserSessionRedisDao extends RedisDao {
    private final RedisTemplate<String, UserSession> redisTemplate;

    private String hashName;
    private HashOperations<String, String, UserSession> hashOperations;

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
        this.hashName = getHashName(UserSession.class);
        long timeToLive = getTimeToLive(UserSession.class);
        if (timeToLive > 0) {
            this.redisTemplate.expire(hashName, timeToLive, TimeUnit.SECONDS);
        }
    }

    /**
     * Create or update {@link UserSession}.
     *
     * @param session object to store.
     */
    public void save(final UserSession session) {
        hashOperations.put(hashName, session.getSessionId(), session);
    }

    /**
     * Retrieve {@link UserSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link UserSession} or not.
     */
    public Optional<UserSession> get(final String sessionId) {
        return Optional.ofNullable(hashOperations.get(hashName, sessionId));
    }


    public void delete(final String sessionId) {
        hashOperations.delete(hashName, sessionId);
    }
}
