package io.kyberorg.yalsee.utils.session;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import lombok.Data;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * {@link SessionBox} record, which holds both {@link VaadinSession} and bound {@link HttpSession}.
 *
 * @since 3.2
 */
@Data
public final class SessionBoxRecord {
    private final VaadinSession vaadinSession;
    private WrappedSession httpSession;

    /**
     * Creates {@link SessionBoxRecord} by splitting {@link VaadinSession} and {@link HttpSession},
     * which bounds to given {@link VaadinSession}.
     *
     * @param vaadinSession valid {@link VaadinSession} to store to {@link SessionBox}.
     * @return created ready to store {@link SessionBoxRecord}.
     */
    public static SessionBoxRecord of(final VaadinSession vaadinSession) {
        return new SessionBoxRecord(vaadinSession);
    }

    private SessionBoxRecord(final VaadinSession vaadinSession) {
        this.vaadinSession = vaadinSession;
        if (vaadinSession.getSession() != null) {
            httpSession = vaadinSession.getSession();
        }
    }

    /**
     * Does this {@link SessionBoxRecord} contain {@link HttpSession}.
     *
     * @return true - if object contains {@link HttpSession}, false otherwise.
     */
    public boolean hasHttpSession() {
        return Objects.nonNull(httpSession);
    }

    /**
     * Check that stored {@link HttpSession} is no longer active and already gone (invalidated by someone else).
     *
     * @return true if {@link HttpSession} is already invalidated, false if {@link HttpSession} is still active.
     */
    public boolean httpSessionAlreadyInvalidated() {
        boolean httpSessionExists = hasHttpSession();
        if (!httpSessionExists) return true;
        try {
            httpSession.getCreationTime();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    /**
     * Gets stored {@link HttpSession}'s id.
     *
     * @return string with {@link HttpSession} session id or empty string is object has no {@link HttpSession}.
     */
    public String getSessionId() {
        if (hasHttpSession()) {
            return getHttpSession().getId();
        } else {
            return "";
        }
    }

}
