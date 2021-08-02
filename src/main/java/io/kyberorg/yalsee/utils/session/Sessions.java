package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import lombok.Data;

import java.util.Objects;

@Data
public class Sessions {
    private final VaadinSession vaadinSession;
    private WrappedSession httpSession;

    public static Sessions of(final VaadinSession vaadinSession) {
        return new Sessions(vaadinSession);
    }

    private Sessions(final VaadinSession vaadinSession) {
        this.vaadinSession = vaadinSession;
        if (vaadinSession.getSession() != null) {
            httpSession = vaadinSession.getSession();
        }
    }

    public boolean hasHttpSession() {
        return Objects.nonNull(httpSession);
    }
}
