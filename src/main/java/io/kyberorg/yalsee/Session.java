package io.kyberorg.yalsee;

import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Session implements SessionInitListener {
    @Override
    public void sessionInit(SessionInitEvent event) {
        log.info("New Session Created");
        log.info("Session ID: {}", event.getSession().getSession().getId());
        log.info("Session interval: {}", event.getSession().getSession().getMaxInactiveInterval());
    }
}
