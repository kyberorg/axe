package io.kyberorg.yalsee.session;

import com.google.gson.Gson;
import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.events.YalseeSessionUpdatedEvent;
import io.kyberorg.yalsee.redis.serializers.YalseeSessionGsonRedisSerializer;
import io.kyberorg.yalsee.services.YalseeSessionService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Application Session object.
 *
 * @since 3.8
 */
@Data
public class YalseeSession {
    /**
     * Default length of session id.
     */
    public static final int SESSION_ID_LEN = 40;
    public static final String NO_SESSION_STORED_MARKER = "DummySessionId";
    public static final int TIMEOUT_FOR_WARNING_MINUTES = 5;
    private static final String VERSION_FORMAT = "yyMMddHHmmssSSS";

    private final String sessionId = RandomStringUtils.randomAlphanumeric(SESSION_ID_LEN);
    private final Device device;
    private final Flags flags = new Flags();
    private final Settings settings = new Settings();

    private final Date created = AppUtils.now();
    private final Date notValidAfter = Date.from(Instant.now().plusSeconds(sessionTimeout()));
    private long version;

    @Data
    public class Flags {
        /**
         * Defines is banner is already displayed and should not appear once again.
         * Should never be private.
         */
        private boolean cookieBannerAlreadyShown = false;

        /**
         * Temporary flag to simulate User mode until this will be released.
         */
        private boolean userModeEnabled = false;

        /**
         * Did we already warn user about session expiry ?
         */
        private boolean expirationWarningShown = false;

        /**
         * Setter for {@link #cookieBannerAlreadyShown} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param cookieBannerAlreadyShown flag to set.
         */
        public void setCookieBannerAlreadyShown(final boolean cookieBannerAlreadyShown) {
            this.cookieBannerAlreadyShown = cookieBannerAlreadyShown;
            YalseeSession.this.fireUpdateEvent();
        }

        /**
         * Setter for {@link #userModeEnabled} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param userModeEnabled flag to set.
         */
        public void setUserModeEnabled(final boolean userModeEnabled) {
            this.userModeEnabled = userModeEnabled;
            YalseeSession.this.fireUpdateEvent();
        }

        /**
         * Setter for {@link #expirationWarningShown} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param expirationWarningShown flag to set.
         */
        public void setExpirationWarningShown(final boolean expirationWarningShown) {
            this.expirationWarningShown = expirationWarningShown;
            YalseeSession.this.fireUpdateEvent();
        }

        @SneakyThrows
        private void fixLink(final YalseeSession parent) {
            Field field = Flags.class.getDeclaredField("this$0");
            field.setAccessible(true);
            field.set(this, parent);
        }
    }

    @Data
    public class Settings {
        /**
         * Allow analytics cookies or not. Should never be private.
         */
        private boolean analyticsCookiesAllowed = false;

        /**
         * Setter for {@link #analyticsCookiesAllowed} property, which fires {@link YalseeSessionUpdatedEvent}.
         *
         * @param analyticsCookiesAllowed flag to set.
         */
        public void setAnalyticsCookiesAllowed(final boolean analyticsCookiesAllowed) {
            this.analyticsCookiesAllowed = analyticsCookiesAllowed;
            YalseeSession.this.fireUpdateEvent();
        }

        @SneakyThrows
        private void fixLink(final YalseeSession parent) {
            Field field = Settings.class.getDeclaredField("this$0");
            field.setAccessible(true);
            field.set(this, parent);
        }
    }

    /**
     * Stores given session to current {@link VaadinSession#getCurrent()} if it is available.
     *
     * @param session {@link YalseeSession} to store.
     * @throws IllegalStateException when no {@link VaadinSession} available.
     */
    public static void setCurrent(final YalseeSession session) {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            throw new IllegalStateException("No VaadinSession at this scope");
        }

        if (session == null) {
            vaadinSession.setAttribute(YalseeSession.class.getSimpleName(), NO_SESSION_STORED_MARKER);
        } else {
            vaadinSession.setAttribute(YalseeSession.class.getSimpleName(), session.getSessionId());
        }
    }

    /**
     * Gets the currently used session.
     * The current session is automatically defined where {@link VaadinSession#getCurrent()} is available.
     * In other cases, (e.g. from background threads), the current session is not automatically defined.
     *
     * @return the current session instance if available, otherwise {@link Optional#empty()}.
     */
    public static Optional<YalseeSession> getCurrent() {
        final VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            return Optional.empty();
        }
        String sessionId = (String) VaadinSession.getCurrent().getAttribute(YalseeSession.class.getSimpleName());
        if (StringUtils.isBlank(sessionId) || sessionId.equals(NO_SESSION_STORED_MARKER)) {
            return Optional.empty();
        }
        if (YalseeSessionService.getInstance() != null) {
            return YalseeSessionService.getInstance().getSession(sessionId);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creating empty {@link YalseeSession}. Constructor for {@link Gson}.
     */
    public YalseeSession() {
        this.device = null;
        this.updateVersion();
    }

    /**
     * Creates session for given {@link Device}.
     *
     * @param device device linked with {@link YalseeSession}.
     */
    public YalseeSession(final Device device) {
        this.device = device;
        this.updateVersion();
    }

    /**
     * Is current session no longer valid.
     *
     * @return true - when session not valid anymore, false if it is still valid.
     */
    public boolean expired() {
        return notValidAfter.before(Date.from(Instant.now()));
    }

    /**
     * Indicates that session expires in less than {@link #TIMEOUT_FOR_WARNING_MINUTES},
     * and it is time to show the warning.
     *
     * @return true if session ttl more than {@link #TIMEOUT_FOR_WARNING_MINUTES}, false if not.
     */
    public boolean isAlmostExpired() {
        long sessionTTL = ChronoUnit.MINUTES.between(AppUtils.now().toInstant(), this.notValidAfter.toInstant());
        return sessionTTL <= TIMEOUT_FOR_WARNING_MINUTES;
    }

    /**
     * This method recovers references to this$0 in nested classes, which happens after {@link Gson} deserialize object.
     * Only {@link YalseeSessionGsonRedisSerializer#deserialize(byte[])} should use it.
     */
    public void fixObjectLinksAfterDeserialization() {
        flags.fixLink(this);
        settings.fixLink(this);
    }

    /**
     * Updates current Session Version.
     */
    public void updateVersion() {
        final String versionString = new SimpleDateFormat(VERSION_FORMAT).format(AppUtils.now());
        version = Long.parseLong(versionString);
    }

    /**
     * Compares versions and calculates if current session is newer (has bigger version) then another.
     *
     * @param anotherSession another {@link YalseeSession} to compare with.
     * @return true if current session is newer than compared one, false if not.
     */
    public boolean isNewer(final YalseeSession anotherSession) {
        if (anotherSession == null || anotherSession.getVersion() == 0) return false;
        return this.getVersion() > anotherSession.getVersion();
    }

    /**
     * Compares versions and calculates if current session is older (has smaller version) then another.
     *
     * @param anotherSession another {@link YalseeSession} to compare with.
     * @return true if current session is older than compared one, false if not.
     */
    public boolean isOlder(final YalseeSession anotherSession) {
        if (anotherSession == null || anotherSession.getVersion() == 0) return false;
        return this.getVersion() < anotherSession.getVersion();
    }

    private void fireUpdateEvent() {
        EventBus.getDefault().post(YalseeSessionUpdatedEvent.createWith(this));
    }

    private int sessionTimeout() {
        return AppUtils.getSessionTimeoutFromStaticContext();
    }

}
