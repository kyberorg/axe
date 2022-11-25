package pm.axe.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pm.axe.redis.dao.AxeSessionRedisDao;
import pm.axe.redis.pubsub.AxeRedisMessage;
import pm.axe.redis.pubsub.MessageEvent;
import pm.axe.redis.pubsub.RedisMessageSender;
import pm.axe.session.AxeSession;
import pm.axe.session.Device;
import pm.axe.session.SessionBox;
import pm.axe.utils.AppUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AxeSessionService {
    private static final String TAG = "[" + AxeSessionService.class.getSimpleName() + "]";
    private static AxeSessionService instance;

    private final AxeSessionRedisDao redisDao;
    private final RedisMessageSender redisMessageSender;

    /**
     * Makes {@link AxeSessionService} be accessible from static context aka POJO.
     *
     * @return {@link AxeSessionService} object stored by {@link #init()}.
     */
    public static AxeSessionService getInstance() {
        return instance;
    }

    @PostConstruct
    private void init() {
        AxeSessionService.instance = this;
    }

    /**
     * Creates new {@link AxeSession}.
     *
     * @param device linked {@link Device} session created from.
     * @return brand-new session with filled in {@link Device} info.
     * @throws IllegalArgumentException when {@link Device} object is {@code null}.
     */
    public AxeSession createNew(final Device device) {
        if (device == null) throw new IllegalArgumentException("Device cannot be null");
        AxeSession newSession = new AxeSession(device);
        storeSession(newSession);
        return newSession;
    }

    /**
     * Stores already created session.
     *
     * @param axeSession session to store
     */
    public void storeSession(final AxeSession axeSession) {
        SessionBox.storeSession(axeSession);
        saveNewSessionInRedis(axeSession);

    }

    /**
     * Creates new session in Redis. Dedicates to make Redis-related operation async.
     * Needs to be overridable (not private).
     *
     * @param session valid Axe Session to create.
     */
    @Async
    void saveNewSessionInRedis(final AxeSession session) {
        try {
            redisDao.create(session);
        } catch (Exception e) {
            log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
        }
    }

    /**
     * Gets {@link AxeSession} by Session ID.
     *
     * @param sessionId string with session id to search against.
     * @return {@link AxeSession} linked to given session if found or {@code null}.
     */
    public Optional<AxeSession> getSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return Optional.empty();

        Optional<AxeSession> axeSession;
        axeSession = Optional.ofNullable(SessionBox.getSession(sessionId));
        if (axeSession.isPresent()) {
            //hit
            if (!axeSession.get().expired()) {
                return axeSession;
            } else {
                //got expired session
                this.destroySession(axeSession.get());
                return Optional.empty();
            }
        } else {
            //miss - redis enabled
            try {
                axeSession = redisDao.get(sessionId);
            } catch (Exception e) {
                log.warn("{} failed to get {} from Redis. Reason: {}",
                        TAG, AxeSession.class.getSimpleName(), e);
                return Optional.empty();
            }

            if (axeSession.isPresent()) {
                //got session from Redis - catching to local
                SessionBox.storeSession(axeSession.get());
                return axeSession;
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Launches Sessions synchronization between local storage and Redis.
     *
     * @param sessionsToSync sessions to synchronize.
     */
    @SneakyThrows
    public void syncSessions(final Collection<AxeSession> sessionsToSync) {
        if (sessionsToSync.size() > 0) {
            if (redisDao.hasLock()) {
                log.warn("{} skipping sync. Sync is locked.", TAG);
                return;
            }
            redisDao.acquireLock(AppUtils.getHostname());
            sessionsToSync.forEach(this::updateSession);
            redisDao.releaseLock(AppUtils.getHostname());
        }
    }

    /**
     * Stores {@link AxeSession} to Redis.
     *
     * @param session object to save
     * @throws IllegalArgumentException if {@link AxeSession} is {@code null}.
     */
    @Async
    void updateSession(final AxeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            if (redisDao.has(session.getSessionId())) {
                Optional<AxeSession> redisSession = redisDao.get(session.getSessionId());
                if (redisSession.isEmpty()) return;
                syncSessionToRedis(session, redisSession.get());
            }
        } catch (Exception e) {
            log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
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
        Optional<AxeSession> localSession = Optional.ofNullable(SessionBox.getSession(sessionId));
        Optional<AxeSession> redisSession = redisDao.get(sessionId);

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
     * @param session {@link AxeSession} object to delete.
     * @throws IllegalArgumentException if {@link AxeSession} is {@code null}.
     */
    @Async
    public void destroySession(final AxeSession session) {
        SessionBox.removeSession(session);
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            redisDao.delete(session.getSessionId());
            AxeRedisMessage deleteMessage = createDeleteMessage(session.getSessionId());
            redisMessageSender.sendMessage(deleteMessage);
        } catch (Exception e) {
            log.error("{} failed to delete user session from Redis. Session ID: {}. Reason: {}",
                    TAG, session.getSessionId(), e.getMessage());
            log.debug("{} exception: {}", TAG, e);
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
        Optional<AxeSession> localSession = Optional.ofNullable(SessionBox.getSession(sessionId));
        if (localSession.isPresent()) {
            log.debug("{} deleting session '{}' locally as well", TAG, sessionId);
            SessionBox.removeSession(localSession.get());
        } else {
            log.debug("{} we don't have session '{}' locally. Ignoring Remote Deletion event.", TAG, sessionId);
        }
    }

    private void syncSessionToRedis(final AxeSession localSession, final AxeSession remoteSession) {
        if (localSession == null || remoteSession == null) throw new IllegalArgumentException("both should be present");
        boolean sessionChanged = localSession.differsFrom(remoteSession);
        if (sessionChanged) {
            if (localSession.isNewer(remoteSession)) {
                log.debug("{} syncing session '{}'. Local -> Redis", TAG, localSession.getSessionId());
                redisDao.update(localSession);
                AxeRedisMessage updateMessage = createUpdateMessage(localSession.getSessionId());
                redisMessageSender.sendMessage(updateMessage);
            } else {
                //redis version is newer - updating
                syncSessionFromRedis(localSession, remoteSession);
            }
        }
    }

    private void syncSessionFromRedis(final AxeSession localSession, final AxeSession remoteSession) {
        if (localSession == null || remoteSession == null) throw new IllegalArgumentException("both should be present");
        if (SessionBox.hasSession(localSession.getSessionId())) {
            if (remoteSession.isNewer(localSession)) {
                log.debug("{} syncing session '{}'. Redis -> Local", TAG, localSession.getSessionId());
                SessionBox.updateSession(remoteSession);
            }
        }
    }

    private AxeRedisMessage createUpdateMessage(final String sessionId) {
        AxeRedisMessage message = AxeRedisMessage.create(MessageEvent.AXE_SESSION_UPDATED);
        message.setPayload(sessionId);
        return message;
    }

    private AxeRedisMessage createDeleteMessage(final String sessionId) {
        AxeRedisMessage message = AxeRedisMessage.create(MessageEvent.AXE_SESSION_DELETED);
        message.setPayload(sessionId);
        return message;
    }
}
