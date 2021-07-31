package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SessionWatchDog {
    private static final String TAG = "[" + SessionWatchDog.class.getSimpleName() + "]";

    @Scheduled(fixedRate = 5000)
    public void endExpiredVaadinSessions() {
        log.info("{} Starting Session Cleanup", TAG);
        List<VaadinSession> expiredSessions = SessionBox.getVaadinSessions().values().parallelStream()
                .filter(this::isSessionExpired).collect(Collectors.toList());
        log.info("{} found {} expired sessions", TAG, expiredSessions.size());
        expiredSessions.forEach(this::removeFromSessionList);
        expiredSessions.forEach(this::endSession);
    }

    private boolean isSessionExpired(VaadinSession vaadinSession) {
        int timeoutInSeconds = 120;
        Instant sessionCreatedTime = Instant.ofEpochMilli(vaadinSession.getSession().getCreationTime());
        Instant now = Instant.now();
        Instant sessionExpirationTime = sessionCreatedTime.plusSeconds(timeoutInSeconds);
        return now.isAfter(sessionExpirationTime);
    }

    private void removeFromSessionList(VaadinSession vaadinSession) {
        SessionBox.getVaadinSessions().remove(vaadinSession.getSession().getId());
    }

    private void endSession(VaadinSession vaadinSession) {
        log.info("{} Removing expired session {}", TAG, vaadinSession.getSession().getId());
        vaadinSession.getSession().invalidate();
        vaadinSession.close();
    }
}
