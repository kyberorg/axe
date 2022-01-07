package io.kyberorg.yalsee.utils.session;

import io.kyberorg.yalsee.events.YalseeSessionCreatedEvent;
import io.kyberorg.yalsee.events.YalseeSessionDestroyedEvent;
import io.kyberorg.yalsee.events.YalseeSessionUpdatedEvent;
import io.kyberorg.yalsee.services.YalseeSessionService;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static io.kyberorg.yalsee.constants.App.Session.SESSION_WATCHDOG_INTERVAL_MILLIS;

@RequiredArgsConstructor
@Slf4j
@Component
public class SessionWatchdog implements HttpSessionListener {
    private static final String TAG = "[" + SessionWatchdog.class.getSimpleName() + "]";

    private final AppUtils appUtils;
    private final YalseeSessionService sessionService;

    /**
     * {@link EventBus} {@link Subscribe}r registration.
     */
    @PostConstruct
    public void init() {
        EventBus.getDefault().register(this);
    }

    /**
     * Receiver for {@link YalseeSessionCreatedEvent}. Logs session information.
     *
     * @param sessionCreatedEvent event, that indicates that new session was created.
     *                            Should have created {@link YalseeSession} inside.
     */
    @Subscribe
    public void sessionCreated(final YalseeSessionCreatedEvent sessionCreatedEvent) {
        if (sessionCreatedEvent == null || sessionCreatedEvent.getYalseeSession() == null) return;
        final YalseeSession session = sessionCreatedEvent.getYalseeSession();
        log.info("{} {} created {} (UA: {}, IP: {})",
                TAG, YalseeSession.class.getSimpleName(), session.getSessionId(),
                session.getDevice().getUserAgent(), session.getDevice().getIp());
    }

    /**
     * Launches Session syncronization aka updates stored session object.
     *
     * @param sessionUpdatedEvent event, that indicates that session values were modified, and
     *                            it is time to update storages with new values.
     *                            Should have affected {@link YalseeSession} inside.
     */
    @Subscribe
    public void syncSession(final YalseeSessionUpdatedEvent sessionUpdatedEvent) {
        if (sessionUpdatedEvent == null || sessionUpdatedEvent.getYalseeSession() == null) return;
        sessionService.updateSession(sessionUpdatedEvent.getYalseeSession());
        log.trace("{} {} updated {}",
                TAG, YalseeSession.class.getSimpleName(), sessionUpdatedEvent.getYalseeSession().getSessionId());
    }

    /**
     * Receiver for {@link YalseeSessionDestroyedEvent}. Logs session information.
     *
     * @param sessionDestroyedEvent event, that indicates that  session was destroyed.
     *                              Should have destroyed {@link YalseeSession} inside.
     */
    @Subscribe
    public void sessionDestroyed(final YalseeSessionDestroyedEvent sessionDestroyedEvent) {
        YalseeSession destroyedSession = sessionDestroyedEvent.getYalseeSession();
        if (destroyedSession == null) {
            log.debug("{} {} destroyed", TAG, YalseeSession.class.getSimpleName());
        } else {
            log.debug("{} {} destroyed {}. Session Age: {}",
                    TAG, YalseeSession.class.getSimpleName(), destroyedSession.getSessionId(),
                    Duration.ofMillis(System.currentTimeMillis() - destroyedSession.getCreated().getTime())
            );
        }
    }

    /**
     * Finds expired sessions, ends 'em and removes from {@link SessionBox}.
     */
    @Scheduled(fixedRate = SESSION_WATCHDOG_INTERVAL_MILLIS)
    public void endExpiredVaadinSessions() {
        log.debug("{} Starting Session Cleanup", TAG);
        //removing already invalidated sessions from list as reading their attributes leads to exceptions.
        SessionBox.getSessions().values().stream()
                .filter(SessionBoxRecord::hasHttpSession)
                .filter(SessionBoxRecord::httpSessionAlreadyInvalidated).map(SessionBoxRecord::getSessionId)
                .forEach(this::removeSessionFromBox);

        //searching for expired sessions
        List<SessionBoxRecord> expiredSessions = SessionBox.getSessions().values().parallelStream()
                .filter(SessionBoxRecord::hasHttpSession)
                .filter(this::isSessionExpired).collect(Collectors.toList());

        if (expiredSessions.isEmpty()) {
            log.debug("{} no expired sessions found", TAG);
        } else {
            log.debug("{} found {} expired sessions", TAG, expiredSessions.size());
        }

        expiredSessions.forEach(this::endSession);
    }

    /**
     * Finds expired sessions and removes 'em from {@link SessionBox} and Redis.
     */
    @Scheduled(fixedRate = SESSION_WATCHDOG_INTERVAL_MILLIS)
    public void endExpiredYalseeSessions() {
        SessionBox.getAllSessions().stream()
                .filter(YalseeSession::expired)
                .forEach(this::removeExpiredSession);
    }

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        log.debug("{} HTTP session created {}", TAG, se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        log.debug("{} HTTP session Destroyed {}, Session age: {}", TAG, se.getSession().getId(),
                Duration.ofMillis(System.currentTimeMillis() - se.getSession().getCreationTime()));
    }

    private boolean isSessionExpired(final SessionBoxRecord sessionBoxRecord) {
        int timeoutInSeconds = appUtils.getSessionTimeout();
        if (!sessionBoxRecord.hasHttpSession()) {
            return true;
        }
        final long sessionCreatedAt;
        try {
            sessionCreatedAt = sessionBoxRecord.getHttpSession().getCreationTime();
        } catch (IllegalStateException e) {
            //this exception is thrown only if this method is called on an invalidated session
            return true;
        }
        Instant sessionCreatedTime = Instant.ofEpochMilli(sessionCreatedAt);
        Instant now = Instant.now();
        Instant sessionExpirationTime = sessionCreatedTime.plusSeconds(timeoutInSeconds);
        return now.isAfter(sessionExpirationTime);
    }

    private void endSession(final SessionBoxRecord sessionBoxRecord) {
        log.debug("{} Removing expired session {}", TAG, sessionBoxRecord.getHttpSession().getId());
        appUtils.endSession(sessionBoxRecord);
    }

    private void removeSessionFromBox(final String sessionId) {
        log.debug("{} removing already gone session from {}. Session ID: {} ",
                TAG, SessionBox.class.getSimpleName(), sessionId);
        try {
            SessionBox.getSessions().remove(sessionId);
        } catch (Exception e) {
            log.warn("{} got error while removing session from {}", TAG, SessionBox.class.getSimpleName());
        }
    }

    private void removeExpiredSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        sessionService.destroySession(yalseeSession);
    }

    /**
     * Unregistering {@link EventBus} {@link Subscribe}r.
     */
    @PreDestroy
    public void destroyBean() {
        EventBus.getDefault().unregister(this);
    }
}
