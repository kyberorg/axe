package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.dao.base.RedisDao;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis DAO for {@link YalseeSession}.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class YalseeSessionRedisDao extends RedisDao {
    private final RedisTemplate<String, YalseeSession> redisTemplate;
    private final AppUtils appUtils;

    private ValueOperations<String, YalseeSession> valueOps;

    @PostConstruct
    private void init() {
        this.valueOps = redisTemplate.opsForValue();
    }

    /**
     * Create or update {@link YalseeSession}.
     *
     * @param session object to store.
     */
    public void save(final YalseeSession session) {
        valueOps.set(appendApplicationPrefix() + session.getSessionId(), session, getRecordTtl(), TimeUnit.SECONDS);
    }

    /**
     * Retrieve {@link YalseeSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link YalseeSession} or not.
     */
    public Optional<YalseeSession> get(final String sessionId) {
        return Optional.ofNullable(valueOps.get(appendApplicationPrefix() + sessionId));
    }

    /**
     * Deletes object if it exists.
     *
     * @param sessionId string with session id used as key.
     * @see ValueOperations#getAndDelete(Object)
     */
    public void delete(final String sessionId) {
        valueOps.getAndDelete(appendApplicationPrefix() + sessionId);
    }

    @Override
    protected long getRecordTtl() {
        return appUtils.getSessionTimeout();
    }
}
