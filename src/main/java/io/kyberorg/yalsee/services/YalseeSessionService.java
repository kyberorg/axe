package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.models.dao.YalseeSessionLocalDao;
import io.kyberorg.yalsee.models.dao.YalseeSessionRedisDao;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.session.YalseeSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        YalseeSession newSession = new YalseeSession();
        newSession.setDevice(device);
        localDao.create(newSession);
        if (isRedisEnabled) {
            redisDao.save(newSession);
        }

        log.info("{} Created new {} {} for {} at {}",
                TAG, YalseeSession.class.getSimpleName(), newSession.getSessionId(),
                device.getUserAgent(), device.getIp());
        return newSession;
    }

    /**
     * Stores {@link YalseeSession} to Redis.
     *
     * @param session object to save
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    public void update(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            localDao.update(session);
            if (isRedisEnabled) {
                redisDao.save(session);
            }
        } catch (Exception e) {
            log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
        }
    }

    /**
     * Gets {@link YalseeSession} by Session ID.
     *
     * @param sessionId string with session id to search against.
     * @return {@link YalseeSession} linked to given session if found or {@code null}.
     */
    public Optional<YalseeSession> getSession(final String sessionId) {
        Optional<YalseeSession> yalseeSession = localDao.get(sessionId);
        if (isRedisEnabled && yalseeSession.isEmpty()) {
            try {
                yalseeSession = redisDao.get(sessionId);
                if (yalseeSession.isPresent()) {
                    if (yalseeSession.get().expired()) {
                        //got expired session from Redis - delete from Redis
                        redisDao.delete(yalseeSession.get().getSessionId());
                    } else {
                        //save its copy to localDao
                        localDao.create(yalseeSession.get());
                    }
                }
            } catch (Exception e) {
                log.error("{} unable to get session from Redis. Got exception: {}", TAG, e.getMessage());
                yalseeSession = Optional.empty();
            }
        }
        return yalseeSession;
    }

    /**
     * Removes session from storages (local and remote), if it exists.
     *
     * @param session {@link YalseeSession} object to delete.
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    public void destroySession(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            localDao.delete(session);
            if (isRedisEnabled) {
                redisDao.delete(session.getSessionId());
            }
            log.info("{} {} {} destroyed", TAG, YalseeSession.class.getSimpleName(), session.getSessionId());
        } catch (Exception e) {
            log.error("{} failed to delete user session. Session ID: {}. Reason: {}",
                    TAG, session.getSessionId(), e.getMessage());
        }
    }

}
