package io.kyberorg.yalsee.utils.session;

import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static io.kyberorg.yalsee.constants.App.Session.SESSION_WATCHDOG_INTERVAL_MILLIS;

@Slf4j
@Component
public class SessionWatchdog implements HttpSessionListener {
    private static final String TAG = "[" + SessionWatchdog.class.getSimpleName() + "]";

    private final AppUtils appUtils;

    /**
     * Spring constructor for autowiring.
     *
     * @param appUtils application utils for reading session timeout from and ending session.
     */
    public SessionWatchdog(final AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    /**
     * Finds expired sessions, ends 'em and removes from {@link SessionBox}.
     */
    @Scheduled(fixedRate = SESSION_WATCHDOG_INTERVAL_MILLIS)
    public void endExpiredVaadinSessions() {
        log.debug("{} Starting Session Cleanup", TAG);
        //removing already invalidated sessions from list as reading their attributes leads to exceptions.
        SessionBox.getSessions().values().parallelStream()
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
}
