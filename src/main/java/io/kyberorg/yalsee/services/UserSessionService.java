package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.events.UserSessionUpdatedEvent;
import io.kyberorg.yalsee.models.dao.UserSessionLocalDao;
import io.kyberorg.yalsee.models.dao.UserSessionRedisDao;
import io.kyberorg.yalsee.models.redis.UserSession;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSessionService {
    private static final String TAG = "[" + UserSessionService.class.getSimpleName() + "]";
    private static final String ERR_DEVICE_OBJECT_IS_NULL = "Device object is null";
    private static final String ERR_COOKIE_OBJECT_IS_NULL = "Cookie object is null";
    private static final String ERR_COOKIE_VALUE_IS_EMPTY = "Cookie Value is empty";
    private static final String ERR_MALFORMED_COOKIE_VALUE = "Cookie value length mismatch";
    private static final String ERR_RECORD_NOT_FOUND = "No corresponding record found in Redis";
    private static final String ERR_HACK_ATTEMPT = "Record with ID exists, but info not match. Got Stolen Cookie.";
    private static final String ERR_SESSION_EXPIRED = "Session expired";

    private final UserSessionRedisDao userSessionDao;
    private final UserSessionLocalDao fallbackLocalDao;
    private final AppUtils appUtils;


    @PostConstruct
    public void init() {
        EventBus.getDefault().register(this);
    }

    public UserSession createNew(final Device device) {
        if (device == null) throw new IllegalArgumentException("Device cannot be null");
        UserSession newSession = new UserSession();
        newSession.setDevice(device);
        this.save(newSession);

        log.info("{} Created new {} {} for {} at {}",
                TAG, UserSession.class.getSimpleName(), newSession.getSessionId(),
                device.getUserAgent(), device.getIp());
        return newSession;
    }

    /**
     * Stores {@link UserSession} to Redis.
     *
     * @param session object to save
     */
    public void save(final UserSession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        try {
            fallbackLocalDao.save(session);
            userSessionDao.save(session);
        } catch (Exception e) {
            log.error("{} unable to persist session to Redis. Got exception: {}", TAG, e.getMessage());
        }
    }

    /**
     * Gets {@link UserSession} by Session ID.
     *
     * @param sessionId string with session id to search against.
     * @return {@link UserSession} linked to given session if found or {@code null}.
     */
    public Optional<UserSession> getSession(final String sessionId) {
        Optional<UserSession> userSession;
        try {
            userSession = userSessionDao.get(sessionId);
        } catch (Exception e) {
            log.error("{} unable to get session from Redis. Got exception: {}", TAG, e.getMessage());
            userSession = fallbackLocalDao.get(sessionId);
        }
        return userSession;
    }

    public void destroySession(final UserSession session) {
        try {
            userSessionDao.delete(session.getSessionId());
            fallbackLocalDao.delete(session.getSessionId());
        } catch (Exception e) {
            log.error("{} failed to delete user session. Session ID: {}. Reason: {}",
                    TAG, session.getSessionId(), e.getMessage());
        }
    }

    @Subscribe
    public void onUserSessionUpdate(final UserSessionUpdatedEvent event) {
        log.debug("{} {} received. Updating Session", TAG, UserSessionUpdatedEvent.class.getSimpleName());
        if (event.getSession() != null) {
            event.getSession().setUpdated(AppUtils.now());
            save(event.getSession());
            log.debug("{} Session '{}' updated", TAG, event.getSession().getSessionId());
        }
    }

    public Cookie createCookie(final UserSession us) {
        Cookie cookie = new Cookie(App.CookieNames.USER_SESSION_COOKIE, us.getSessionId());
        cookie.setMaxAge(appUtils.getSessionTimeout());
        cookie.setSecure(us.getDevice().getWebBrowser().isSecureConnection());
        cookie.setHttpOnly(true);
        return cookie;
    }

    public OperationResult checkCookie(Cookie cookie, Device currentDevice) {
        //Device null check
        if (Objects.isNull(currentDevice)) {
            return OperationResult.generalFail().withMessage(ERR_DEVICE_OBJECT_IS_NULL);
        }
        //Cookie Value aka Session Id
        if (Objects.isNull(cookie)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_OBJECT_IS_NULL);
        }
        String cookieValue = cookie.getValue();
        if (StringUtils.isBlank(cookieValue)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_VALUE_IS_EMPTY);
        }
        if (cookieValue.length() != UserSession.SESSION_ID_LEN) {
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_COOKIE_VALUE);
        }

        //lookup by sessionId
        Optional<UserSession> sessionRecord = this.getSession(cookieValue);
        if (sessionRecord.isEmpty()) {
            return OperationResult.elementNotFound().withMessage(ERR_RECORD_NOT_FOUND);
        }
        UserSession session = sessionRecord.get();
        //valid (not expired)
        if (session.expired()) {
            invalidateAffectedSession(session);
            return OperationResult.generalFail().withMessage(ERR_SESSION_EXPIRED);
        }
        //compare devices
        Device savedDevice = session.getDevice();
        if (!currentDevice.isSameDevice(savedDevice)) {
            //device mismatch - stolen cookie
            this.invalidateAffectedSession(session);
            return OperationResult.banned().withMessage(ERR_HACK_ATTEMPT);
        }

        //all good
        return OperationResult.success().addPayload(session);
    }

    public void invalidateAffectedSession(final UserSession session) {
        log.info("{} deleting affected session {}", TAG, session.getSessionId());
        this.destroySession(session);
    }

    @PreDestroy
    public void destroyBean() {
        EventBus.getDefault().unregister(this);
    }
}
