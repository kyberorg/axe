package pm.axe.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pm.axe.constants.App;
import pm.axe.events.session.AxeSessionAlmostExpiredEvent;
import pm.axe.events.session.AxeSessionCreatedEvent;
import pm.axe.events.session.AxeSessionDestroyedEvent;
import pm.axe.services.AxeSessionService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
@Slf4j
@Component
public class SessionWatchdog implements HttpSessionListener {
    private static final String TAG = "[" + SessionWatchdog.class.getSimpleName() + "]";

    private final AxeSessionService sessionService;

    /**
     * {@link EventBus} {@link Subscribe}r registration.
     */
    @PostConstruct
    public void init() {
        EventBus.getDefault().register(this);
    }

    /**
     * Receiver for {@link AxeSessionCreatedEvent}. Logs session information.
     *
     * @param sessionCreatedEvent event, that indicates that new session was created.
     *                            Should have created {@link AxeSession} inside.
     */
    @Subscribe
    public void sessionCreated(final AxeSessionCreatedEvent sessionCreatedEvent) {
        if (sessionCreatedEvent == null || sessionCreatedEvent.getAxeSession() == null) return;
        final AxeSession session = sessionCreatedEvent.getAxeSession();
        log.info("{} {} created {} (UA: {}, IP: {})",
                TAG, AxeSession.class.getSimpleName(), session.getSessionId(),
                session.getDevice().getUserAgent(), session.getDevice().getIp());
    }

    /**
     * Receiver for {@link AxeSessionDestroyedEvent}. Logs session information.
     *
     * @param sessionDestroyedEvent event, that indicates that  session was destroyed.
     *                              Should have destroyed {@link AxeSession} inside.
     */
    @Subscribe
    public void sessionDestroyed(final AxeSessionDestroyedEvent sessionDestroyedEvent) {
        AxeSession destroyedSession = sessionDestroyedEvent.getAxeSession();
        if (destroyedSession == null) {
            log.debug("{} {} destroyed", TAG, AxeSession.class.getSimpleName());
        } else {
            log.debug("{} {} destroyed {}. Session Age: {}",
                    TAG, AxeSession.class.getSimpleName(), destroyedSession.getSessionId(),
                    Duration.ofMillis(System.currentTimeMillis() - destroyedSession.getCreated().getTime())
            );
        }
    }

    /**
     * Launches Sessions synchronization.
     */
    @Scheduled(fixedDelay = App.Session.SESSION_SYNC_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void syncSessions() {
        long existingSessionsCount;
        long newSessionsCount;

        //sync only if session changed (differs from previous)
        List<AxeSession> sessionsToSync = SessionBox.getAllSessions().stream()
                .filter(SessionBox::hasPreviousVersion) //filter sessions with known previous state only
                .filter(session -> session.differsFrom(SessionBox.getPreviousVersion(session))) //only if changed
                .map(session -> SessionBox.logSessionsDiff(SessionBox.getPreviousVersion(session), session)) //log diff
                .map(AxeSession::updateVersion) //since they change - we have to update their versions
                .map(SessionBox::setAsPreviousVersion) //and save them as previous for next sync
                .collect(Collectors.toList()); //and finally add them list for syncing

        existingSessionsCount = sessionsToSync.size();

        //new (un-synced) considered changed as nothing to compare with
        sessionsToSync.addAll(
                SessionBox.getAllSessions().stream()
                        .filter(not(SessionBox::hasPreviousVersion)) //filter new sessions (without previous state)
                        .map(AxeSession::updateVersion) //they are considered as changed - updating their versions
                        .map(SessionBox::setAsPreviousVersion) //and save them as previous for next sync
                        .collect(Collectors.toList())); //and finally add them list for syncing

        newSessionsCount = sessionsToSync.size() - existingSessionsCount;

        if (sessionsToSync.size() > 0) {
            log.info("{} Sync Summary: {} sessions to be synced ({} new, {} existing)",
                    TAG, sessionsToSync.size(), newSessionsCount, existingSessionsCount);
        }

        sessionService.syncSessions(sessionsToSync);
    }

    /**
     * Detects sessions that are about to expire. This method through to show session expiration warning,
     * so it also filters out those sessions where given warning already shown.
     */
    @Scheduled(fixedRate = App.Session.SESSION_WATCHDOG_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void detectAlmostExpiredAxeSessions() {
        SessionBox.getAllSessions().stream()
                .filter(AxeSession::isAlmostExpired)
                .filter(ys -> !ys.getFlags().isExpirationWarningShown())
                .forEach(this::fireAlmostExpiredEvent);
    }

    /**
     * Finds expired sessions and removes 'em from {@link SessionBox} and Redis.
     */
    @Scheduled(fixedDelay = App.Session.SESSION_WATCHDOG_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void endExpiredAxeSessions() {
        SessionBox.getAllSessions().stream()
                .filter(AxeSession::expired)
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

    private void removeExpiredSession(final AxeSession axeSession) {
        if (axeSession == null) return;
        sessionService.destroySession(axeSession);
        SessionBox.deletePreviousVersion(axeSession);
    }

    private void fireAlmostExpiredEvent(final AxeSession session) {
        EventBus.getDefault().post(AxeSessionAlmostExpiredEvent.createWith(session));
    }

    /**
     * Unregistering {@link EventBus} {@link Subscribe}r.
     */
    @PreDestroy
    public void destroyBean() {
        EventBus.getDefault().unregister(this);
    }
}
