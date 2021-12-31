package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.events.YalseeSessionUpdatedEvent;
import io.kyberorg.yalsee.models.dao.YalseeSessionLocalDao;
import io.kyberorg.yalsee.models.dao.YalseeSessionRedisDao;
import io.kyberorg.yalsee.models.redis.YalseeSession;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class YalseeSessionService {
    private static final String TAG = "[" + YalseeSessionService.class.getSimpleName() + "]";

    private final YalseeSessionRedisDao userSessionDao;
    private final YalseeSessionLocalDao fallbackLocalDao;

    /**
     * {@link EventBus} {@link Subscribe}r registration.
     */
    @PostConstruct
    public void init() {
        EventBus.getDefault().register(this);
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
        YalseeSession newSession = new YalseeSession();
        newSession.setDevice(device);
        this.save(newSession);

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
    public void save(final YalseeSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            fallbackLocalDao.save(session);
            userSessionDao.save(session);
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
        Optional<YalseeSession> yalseeSession;
        try {
            yalseeSession = userSessionDao.get(sessionId);
        } catch (Exception e) {
            log.error("{} unable to get session from Redis. Got exception: {}", TAG, e.getMessage());
            yalseeSession = fallbackLocalDao.get(sessionId);
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
            fallbackLocalDao.delete(session.getSessionId());
            userSessionDao.delete(session.getSessionId());
            log.info("{} {} {} destroyed", TAG, YalseeSession.class.getSimpleName(), session.getSessionId());
        } catch (Exception e) {
            log.error("{} failed to delete user session. Session ID: {}. Reason: {}",
                    TAG, session.getSessionId(), e.getMessage());
        }
    }

    /**
     * Method which persist updated {@link YalseeSession}.
     *
     * @param event update event with {@link YalseeSession} inside.
     */
    @Subscribe
    public void onUserSessionUpdate(final YalseeSessionUpdatedEvent event) {
        log.debug("{} {} received. Updating Session", TAG, YalseeSessionUpdatedEvent.class.getSimpleName());
        if (event.getSession() != null) {
            event.getSession().setUpdated(AppUtils.now());
            save(event.getSession());
            log.debug("{} Session '{}' updated", TAG, event.getSession().getSessionId());
        }
    }

    /**
     * Unregistering {@link EventBus} {@link Subscribe}r.
     */
    @PreDestroy
    public void destroyBean() {
        EventBus.getDefault().unregister(this);
    }
}
