package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.models.dao.YalseeSessionLocalDao;
import io.kyberorg.yalsee.models.dao.YalseeSessionRedisDao;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.session.YalseeSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class YalseeSessionService {
    private static final String TAG = "[" + YalseeSessionService.class.getSimpleName() + "]";

    private final YalseeSessionRedisDao redisDao;
    private final YalseeSessionLocalDao localDao;

    @Value("${redis.enabled}")
    private boolean isRedisEnabled;

    /**
     * Creates new {@link YalseeSession}.
     *
     * @param device linked {@link Device} session created from.
     * @return brand-new session with filled in {@link Device} info.
     * @throws IllegalArgumentException when {@link Device} object is {@code null}.
     */
    public YalseeSession createNew(final Device device) {
        if (device == null) throw new IllegalArgumentException("Device cannot be null");
        YalseeSession newSession = new YalseeSession(device);
        if (isRedisEnabled) {
            redisDao.save(newSession);
        }
        localDao.create(newSession);

        return newSession;
    }

    /**
     * Gets {@link YalseeSession} by Session ID.
     *
     * @param sessionId string with session id to search against.
     * @return {@link YalseeSession} linked to given session if found or {@code null}.
     */
    public Optional<YalseeSession> getSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return Optional.empty();

        Optional<YalseeSession> yalseeSession;
        boolean sessionFromRedis = false;

        if (isRedisEnabled) {
            yalseeSession = redisDao.get(sessionId);
            sessionFromRedis = true;
        } else {
            yalseeSession = localDao.get(sessionId);
        }

        if (yalseeSession.isEmpty()) {
            return yalseeSession;
        }

        if (yalseeSession.get().expired()) {
            //got expired session
            this.destroySession(yalseeSession.get());
            return Optional.empty();
        } else if (sessionFromRedis) {
            //got active session from Redis
            this.doBackSync(yalseeSession.get());
        }

        return yalseeSession;
    }

    /**
     * Stores {@link YalseeSession} to Redis.
     *
     * @param session object to save
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    public void updateSession(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        if (isRedisEnabled) {
            try {
                redisDao.save(session);
            } catch (Exception e) {
                log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
            }
        }
        localDao.update(session);
    }

    /**
     * Removes session from storages (local and remote), if it exists.
     *
     * @param session {@link YalseeSession} object to delete.
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    public void destroySession(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        if (isRedisEnabled) {
            try {
                redisDao.delete(session.getSessionId());
            } catch (Exception e) {
                log.error("{} failed to delete user session from Redis. Session ID: {}. Reason: {}",
                        TAG, session.getSessionId(), e.getMessage());
                log.debug("{} exception: {}", TAG, e);
            }
        }
        localDao.delete(session);
    }

    private void doBackSync(final YalseeSession session) {
        if (sessionExistsInLocalStorage(session)) {
            localDao.update(session);
        } else {
            localDao.create(session);
        }
    }

    private boolean sessionExistsInLocalStorage(final YalseeSession session) {
        return localDao.get(session.getSessionId()).isPresent();
    }
}
