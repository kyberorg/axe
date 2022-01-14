package io.kyberorg.yalsee.session;

import io.kyberorg.yalsee.events.YalseeSessionAlmostExpiredEvent;
import io.kyberorg.yalsee.events.YalseeSessionCreatedEvent;
import io.kyberorg.yalsee.events.YalseeSessionDestroyedEvent;
import io.kyberorg.yalsee.services.YalseeSessionService;
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
import java.util.concurrent.TimeUnit;

import static io.kyberorg.yalsee.constants.App.Session.SESSION_SYNC_INTERVAL;
import static io.kyberorg.yalsee.constants.App.Session.SESSION_WATCHDOG_INTERVAL;

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
     * Launches Sessions synchronization.
     */
    @Scheduled(fixedDelay = SESSION_SYNC_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void syncSessions() {
        sessionService.syncSessions();
    }

    /**
     * Detects sessions that are about to expire. This method through to show session expiration warning,
     * so it also filters out those sessions where given warning already shown.
     */
    @Scheduled(fixedRate = SESSION_WATCHDOG_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void detectAlmostExpiredYalseeSessions() {
        SessionBox.getAllSessions().stream()
                .filter(YalseeSession::isAlmostExpired)
                .filter(ys -> !ys.getFlags().isExpirationWarningShown())
                .forEach(this::fireAlmostExpiredEvent);
    }

    /**
     * Finds expired sessions and removes 'em from {@link SessionBox} and Redis.
     */
    @Scheduled(fixedDelay = SESSION_WATCHDOG_INTERVAL, timeUnit = TimeUnit.SECONDS)
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

    private void removeExpiredSession(final YalseeSession yalseeSession) {
        if (yalseeSession == null) return;
        sessionService.destroySession(yalseeSession);
    }

    private void fireAlmostExpiredEvent(final YalseeSession session) {
        EventBus.getDefault().post(YalseeSessionAlmostExpiredEvent.createWith(session));
    }

    /**
     * Unregistering {@link EventBus} {@link Subscribe}r.
     */
    @PreDestroy
    public void destroyBean() {
        EventBus.getDefault().unregister(this);
    }
}
