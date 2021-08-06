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

import static io.kyberorg.yalsee.constants.App.Session.SESSION_WATCH_DOG_INTERVAL_MILLIS;

@Slf4j
@Component
public class SessionWatchDog implements HttpSessionListener {
    private static final String TAG = "[" + SessionWatchDog.class.getSimpleName() + "]";

    private final AppUtils appUtils;

    public SessionWatchDog(final AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    @Scheduled(fixedRate = SESSION_WATCH_DOG_INTERVAL_MILLIS)
    public void endExpiredVaadinSessions() {
        log.debug("{} Starting Session Cleanup", TAG);
        //removing already invalidated sessions from list as reading their attributes leads to exceptions.
        SessionBox.getSessions().values().parallelStream()
                .filter(Sessions::hasHttpSession)
                .filter(Sessions::httpSessionAlreadyInvalidated).map(Sessions::getSessionId)
                .forEach(this::removeSessionFromBox);

        //searching for expired sessions
        List<Sessions> expiredSessions = SessionBox.getSessions().values().parallelStream()
                .filter(Sessions::hasHttpSession)
                .filter(this::isSessionExpired).collect(Collectors.toList());

        if (expiredSessions.isEmpty()) {
            log.debug("{} no expired sessions found", TAG);
        } else {
            log.debug("{} found {} expired sessions", TAG, expiredSessions.size());
        }

        expiredSessions.forEach(this::endSession);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.debug("{} HTTP session created {}", TAG, se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("{} HTTP session Destroyed {}, Session age: {}", TAG, se.getSession().getId(),
                Duration.ofMillis(System.currentTimeMillis() - se.getSession().getCreationTime()));
    }

    private boolean isSessionExpired(final Sessions sessions) {
        int timeoutInSeconds = appUtils.getSessionTimeout();
        Instant sessionCreatedTime = Instant.ofEpochMilli(sessions.getHttpSession().getCreationTime());
        Instant now = Instant.now();
        Instant sessionExpirationTime = sessionCreatedTime.plusSeconds(timeoutInSeconds);
        return now.isAfter(sessionExpirationTime);
    }

    private void endSession(final Sessions sessions) {
        log.debug("{} Removing expired session {}", TAG, sessions.getHttpSession().getId());
        appUtils.endSession(sessions);
    }

    private void removeSessionFromBox(String sessionId) {
        log.debug("{} removing already gone session from {}. Session ID: {} ",
                TAG, SessionBox.class.getSimpleName(), sessionId);
        SessionBox.getSessions().remove(sessionId);
    }
}
