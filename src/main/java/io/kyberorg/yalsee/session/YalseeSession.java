package io.kyberorg.yalsee.session;

import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.events.YalseeSessionUpdatedEvent;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * Application Session object.
 *
 * @since 3.8
 */
@Data
@NoArgsConstructor
public class YalseeSession implements Serializable {
    /**
     * Default length of session id.
     */
    public static final int SESSION_ID_LEN = 40;

    private final String sessionId = RandomStringUtils.randomAlphanumeric(SESSION_ID_LEN);
    private Device device;
    private final Flags flags = new Flags();
    private final Settings settings = new Settings();

    private final Date created = AppUtils.now();
    private final Date notValidAfter = Date.from(Instant.now()
            .plusSeconds(AppUtils.getSessionTimeoutFromStaticContext()));

    /**
     * Stores given session to current {@link VaadinSession#getCurrent()} if it is available.
     *
     * @param session {@link YalseeSession} to store.
     * @throws IllegalArgumentException when session is null
     * @throws IllegalStateException    when no {@link VaadinSession} available.
     */
    public static void setCurrent(final YalseeSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }

        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            throw new IllegalStateException("No VaadinSession at this scope");
        }

        vaadinSession.setAttribute(YalseeSession.class, session);
    }

    /**
     * Gets the currently used session.
     * The current session is automatically defined where {@link VaadinSession#getCurrent()} is available.
     * In other cases, (e.g. from background threads), the current session is not automatically defined.
     *
     * @return the current session instance if available, otherwise {@code null}.
     */
    public static YalseeSession getCurrent() {
        final VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            return null;
        }
        return vaadinSession.getAttribute(YalseeSession.class);
    }

    /**
     * Is current session no longer valid.
     *
     * @return true - when session not valid anymore, false if it is still valid.
     */
    public boolean expired() {
        return notValidAfter.before(Date.from(Instant.now()));
    }

    private void fireUpdateEvent() {
        EventBus.getDefault().post(YalseeSessionUpdatedEvent.createWith(this));
    }

    @Data
    public class Flags implements Serializable {
        /**
         * Defines is banner is already displayed and should not appear once again.
         * Should never be private.
         */
        boolean cookieBannerAlreadyShown = false;

        /**
         * Setter for {@link #cookieBannerAlreadyShown} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param cookieBannerAlreadyShown flag to set.
         */
        public void setCookieBannerAlreadyShown(final boolean cookieBannerAlreadyShown) {
            this.cookieBannerAlreadyShown = cookieBannerAlreadyShown;
            YalseeSession.this.fireUpdateEvent();
        }
    }

    public class Settings implements Serializable {
        /**
         * Allow analytics cookies or not. Should never be private.
         */
        boolean analyticsCookiesAllowed = false;

        /**
         * Setter for {@link #analyticsCookiesAllowed} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param analyticsCookiesAllowed flag to set.
         */
        public void setAnalyticsCookiesAllowed(final boolean analyticsCookiesAllowed) {
            this.analyticsCookiesAllowed = analyticsCookiesAllowed;
            YalseeSession.this.fireUpdateEvent();
        }
    }
}
