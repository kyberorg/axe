package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.dao.base.RedisDao;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private static final int LOCK_TTL_SECONDS = 7;

    private final RedisTemplate<String, YalseeSession> redisTemplate;
    private final RedisTemplate<String, String> lockTemplate;
    private final AppUtils appUtils;

    private static String lockName = RedisDao.getApplicationPrefix() + "SessionUpdateLock";

    private ValueOperations<String, YalseeSession> valueOps;
    private ValueOperations<String, String> lockOps;

    @PostConstruct
    private void init() {
        this.valueOps = redisTemplate.opsForValue();
        this.lockOps = lockTemplate.opsForValue();
        lockName = updateLockNameAtRuntime();
    }

    /**
     * Creates {@link YalseeSession}.
     *
     * @param session object to store.
     * @throws IllegalArgumentException when session is null.
     */
    public void create(final YalseeSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session to create is null");
        }
        valueOps.set(getApplicationPrefix() + session.getSessionId(), session, getRecordTtl(), TimeUnit.SECONDS);
    }

    /**
     * Checks if {@link YalseeSession} with given ID exists in Redis or not.
     *
     * @param sessionId non-empty string with session id to check.
     * @return true if {@link YalseeSession} found, false - if not.
     * @throws IllegalArgumentException when session id is empty or null.
     */
    public boolean has(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) throw new IllegalArgumentException("Session to search is null");
        return exists(sessionId);
    }

    /**
     * Retrieve {@link YalseeSession} by its ID.
     *
     * @param sessionId string with session id used as key.
     * @return {@link Optional} which contains {@link YalseeSession} or not.
     * @throws IllegalArgumentException when session io is empty or null.
     */
    public Optional<YalseeSession> get(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) throw new IllegalArgumentException("Session to retrieve is null");
        if (!exists(sessionId)) return Optional.empty();
        return Optional.ofNullable(valueOps.get(getApplicationPrefix() + sessionId));
    }

    /**
     * Updates {@link YalseeSession} and preserves TTL.
     *
     * @param session object to store.
     * @throws IllegalArgumentException when session is null.
     * @see #getRecordTtl()
     */
    public void update(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session to update is null");
        final String keyName = getApplicationPrefix() + session.getSessionId();
        final Long currentTTL = redisTemplate.getExpire(keyName, TimeUnit.SECONDS);
        final long ttl = currentTTL != null ? currentTTL : getRecordTtl();
        valueOps.setIfPresent(getApplicationPrefix() + session.getSessionId(), session, ttl, TimeUnit.SECONDS);
    }

    /**
     * Deletes object if it exists.
     *
     * @param sessionId string with session id used as key.
     * @throws IllegalArgumentException when session is null.
     * @see ValueOperations#getAndDelete(Object)
     */
    public void delete(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) throw new IllegalArgumentException("Session to delete is null");
        if (exists(sessionId)) {
            valueOps.getAndDelete(getApplicationPrefix() + sessionId);
        }
    }

    /**
     * Checks if Session update lock is already exists.
     *
     * @return true if lock found, false if not.
     */
    public boolean hasLock() {
        return Boolean.TRUE.equals(lockTemplate.hasKey(lockName));
    }

    /**
     * Sets new Session update lock.
     *
     * @param lockOwner owner of lock. Normally system hostname.
     * @throws IllegalAccessException   when lock is already set.
     * @throws IllegalArgumentException when lock owner is not set (empty or null string).
     * @see AppUtils#getHostname()
     */
    public void acquireLock(final String lockOwner) throws IllegalAccessException {
        if (StringUtils.isBlank(lockOwner)) throw new IllegalArgumentException("Lock owner is empty");
        if (hasLock()) throw new IllegalAccessException("Lock is already set");
        lockOps.setIfAbsent(lockName, lockOwner, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Deletes Session update lock.
     *
     * @param lockOwner owner of lock. Normally system hostname.
     * @throws IllegalAccessException   when attempted to delete not-owned lock.
     * @throws IllegalArgumentException when lock owner is not set (empty or null string).
     * @see AppUtils#getHostname()
     */
    public void releaseLock(final String lockOwner) throws IllegalAccessException {
        if (StringUtils.isBlank(lockOwner)) throw new IllegalArgumentException("Lock owner is empty");
        if (hasLock()) {
            String lock = getLock();
            if (lockOwner.equals(lock)) {
                lockOps.getAndDelete(lockName);
            } else {
                throw new IllegalAccessException("Cannot delete lock. Wrong owner.");
            }
        }
    }

    private String getLock() {
        return lockOps.get(lockName);
    }

    @Override
    protected long getRecordTtl() {
        return appUtils.getSessionTimeout();
    }

    private boolean exists(final String sessionId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getApplicationPrefix() + sessionId));
    }

    private String updateLockNameAtRuntime() {
        return RedisDao.getApplicationPrefix() + "SessionUpdateLock";
    }
}
