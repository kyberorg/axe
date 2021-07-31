package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static io.kyberorg.yalsee.constants.App.Session.SESSION_WATCH_DOG_INTERVAL_MILLIS;

@Slf4j
@Component
public class SessionWatchDog {
    private static final String TAG = "[" + SessionWatchDog.class.getSimpleName() + "]";

    private final AppUtils appUtils;

    public SessionWatchDog(final AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    @Scheduled(fixedRate = SESSION_WATCH_DOG_INTERVAL_MILLIS)
    public void endExpiredVaadinSessions() {
        log.debug("{} Starting Session Cleanup", TAG);
        List<VaadinSession> expiredSessions = SessionBox.getVaadinSessions().values().parallelStream()
                .filter(this::isSessionExpired).collect(Collectors.toList());

        if (expiredSessions.isEmpty()) {
            log.debug("{} no expired sessions found", TAG);
        } else {
            log.debug("{} found {} expired sessions", TAG, expiredSessions.size());
        }

        expiredSessions.forEach(this::endSession);
    }

    private boolean isSessionExpired(VaadinSession vaadinSession) {
        int timeoutInSeconds = appUtils.getSessionTimeout();
        Instant sessionCreatedTime = Instant.ofEpochMilli(vaadinSession.getSession().getCreationTime());
        Instant now = Instant.now();
        Instant sessionExpirationTime = sessionCreatedTime.plusSeconds(timeoutInSeconds);
        return now.isAfter(sessionExpirationTime);
    }

    private void endSession(VaadinSession vaadinSession) {
        log.debug("{} Removing expired session {}", TAG, vaadinSession.getSession().getId());
        appUtils.endVaadinSession(vaadinSession);
    }
}
