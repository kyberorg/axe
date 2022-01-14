package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.models.dao.YalseeSessionLocalDao;
import io.kyberorg.yalsee.models.dao.YalseeSessionRedisDao;
import io.kyberorg.yalsee.redis.pubsub.MessageEvent;
import io.kyberorg.yalsee.redis.pubsub.RedisMessageSender;
import io.kyberorg.yalsee.redis.pubsub.YalseeMessage;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class YalseeSessionService {
    private static final String TAG = "[" + YalseeSessionService.class.getSimpleName() + "]";
    private static YalseeSessionService instance;

    private final YalseeSessionRedisDao redisDao;
    private final YalseeSessionLocalDao localDao;
    private final RedisMessageSender redisMessageSender;

    @Value("${redis.enabled}")
    private boolean isRedisEnabled;

    /**
     * Makes {@link YalseeSessionService} be accessible from static context aka POJO.
     *
     * @return {@link YalseeSessionService} object stored by {@link #init()}.
     */
    public static YalseeSessionService getInstance() {
        return instance;
    }

    @PostConstruct
    private void init() {
        YalseeSessionService.instance = this;
    }

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
        storeSession(newSession);
        return newSession;
    }

    /**
     * Stores already created session.
     *
     * @param yalseeSession session to store
     */
    public void storeSession(final YalseeSession yalseeSession) {
        localDao.create(yalseeSession);
        if (isRedisEnabled) {
            saveNewSessionInRedis(yalseeSession);
        }
    }

    /**
     * Creates new session in Redis. Dedicates to make Redis-related operation async.
     * Needs to be overridable (not private).
     *
     * @param session valid Yalsee Session to create.
     */
    @Async
    void saveNewSessionInRedis(final YalseeSession session) {
        try {
            redisDao.create(session);
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
        if (StringUtils.isBlank(sessionId)) return Optional.empty();

        Optional<YalseeSession> yalseeSession;
        yalseeSession = localDao.get(sessionId);
        if (yalseeSession.isPresent()) {
            //hit
            if (!yalseeSession.get().expired()) {
                return yalseeSession;
            } else {
                //got expired session
                this.destroySession(yalseeSession.get());
                return Optional.empty();
            }
        } else if (isRedisEnabled) {
            //miss - redis enabled
            try {
                yalseeSession = redisDao.get(sessionId);
            } catch (Exception e) {
                log.warn("{} failed to get {} from Redis. Reason: {}",
                        TAG, YalseeSession.class.getSimpleName(), e);
                return Optional.empty();
            }

            if (yalseeSession.isPresent()) {
                //got session from Redis - catching to local
                localDao.create(yalseeSession.get());
                return yalseeSession;
            } else {
                return Optional.empty();
            }
        } else {
            //miss - redis disabled
            return Optional.empty();
        }
    }

    /**
     * Launches Sessions synchronization between local storage and Redis.
     */
    @SneakyThrows
    public void syncSessions() {
        if (isRedisEnabled) {
            if (redisDao.hasLock()) {
                log.warn("{} skipping sync. Sync is locked.", TAG);
                return;
            }
            redisDao.acquireLock(AppUtils.getHostname());
            localDao.getAllSessions().forEach(this::updateSession);
            redisDao.releaseLock(AppUtils.getHostname());
        }
    }

    /**
     * Stores {@link YalseeSession} to Redis.
     *
     * @param session object to save
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    @Async
    void updateSession(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        if (isRedisEnabled) {
            try {
                if (redisDao.has(session.getSessionId())) {
                    Optional<YalseeSession> redisSession = redisDao.get(session.getSessionId());
                    if (redisSession.isEmpty()) return;
                    syncSessionToRedis(session, redisSession.get());
                }
            } catch (Exception e) {
                log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
            }
        }
    }

    /**
     * Handles session update that was done by another instances.
     *
     * @param sessionId string with affected session id
     */
    @Async
    public void onRemoteUpdate(final String sessionId) {
        log.debug("{} Got Remote Update.", TAG);
        Optional<YalseeSession> localSession = localDao.get(sessionId);
        Optional<YalseeSession> redisSession = redisDao.get(sessionId);

        if (localSession.isPresent() && redisSession.isPresent()) {
            syncSessionFromRedis(localSession.get(), redisSession.get());
        } else if (redisSession.isPresent()) {
            log.debug("{} we don't have session '{}' locally. Ignoring Remote Update.", TAG, sessionId);
        } else {
            //should never happen
            log.debug("{} there is no session '{}' in Redis. No Reason to sync it", TAG, sessionId);
        }
    }

    /**
     * Removes session from storages (local and remote), if it exists.
     *
     * @param session {@link YalseeSession} object to delete.
     * @throws IllegalArgumentException if {@link YalseeSession} is {@code null}.
     */
    @Async
    public void destroySession(final YalseeSession session) {
        localDao.delete(session);
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        if (isRedisEnabled) {
            try {
                redisDao.delete(session.getSessionId());
                YalseeMessage deleteMessage = createDeleteMessage(session.getSessionId());
                redisMessageSender.sendMessage(deleteMessage);
            } catch (Exception e) {
                log.error("{} failed to delete user session from Redis. Session ID: {}. Reason: {}",
                        TAG, session.getSessionId(), e.getMessage());
                log.debug("{} exception: {}", TAG, e);
            }
        }
    }

    /**
     * Handles session deletion that was done by another instances.
     *
     * @param sessionId string with affected session id
     */
    @Async
    public void onRemoteDeletion(final String sessionId) {
        log.debug("{} Remote Session deleted", TAG);
        Optional<YalseeSession> localSession = localDao.get(sessionId);
        if (localSession.isPresent()) {
            log.debug("{} deleting session '{}' locally as well", TAG, sessionId);
            localDao.delete(localSession.get());
        } else {
            log.debug("{} we don't have session '{}' locally. Ignoring Remote Deletion event.", TAG, sessionId);
        }
    }

    private void syncSessionToRedis(final YalseeSession localSession, final YalseeSession remoteSession) {
        boolean sessionChanges = localSession.hashCode() != remoteSession.hashCode();
        if (sessionChanges) {
            localSession.updateVersion();
            if (localSession.isNewer(remoteSession)) {
                log.debug("{} syncing session '{}'. Local -> Redis", TAG, localSession.getSessionId());
                redisDao.update(localSession);
                YalseeMessage updateMessage = createUpdateMessage(localSession.getSessionId());
                redisMessageSender.sendMessage(updateMessage);
            } else {
                //redis version is newer - updating
                syncSessionFromRedis(localSession, remoteSession);
            }
        }
    }

    private void syncSessionFromRedis(final YalseeSession localSession, final YalseeSession remoteSession) {
        if (localDao.has(localSession.getSessionId())) {
            if (remoteSession.isNewer(localSession)) {
                log.debug("{} syncing session '{}'. Redis -> Local", TAG, localSession.getSessionId());
                localDao.update(remoteSession);
            }
        }
    }

    private YalseeMessage createUpdateMessage(final String sessionId) {
        YalseeMessage message = YalseeMessage.create(MessageEvent.YALSEE_SESSION_UPDATED);
        message.setPayload(sessionId);
        return message;
    }

    private YalseeMessage createDeleteMessage(final String sessionId) {
        YalseeMessage message = YalseeMessage.create(MessageEvent.YALSEE_SESSION_DELETED);
        message.setPayload(sessionId);
        return message;
    }
}
