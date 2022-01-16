package io.kyberorg.yalsee.session;

import com.google.gson.Gson;
import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.services.YalseeSessionService;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
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

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Flags flags = (Flags) o;
            return cookieBannerAlreadyShown == flags.cookieBannerAlreadyShown
                    && userModeEnabled == flags.userModeEnabled
                    && expirationWarningShown == flags.expirationWarningShown;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cookieBannerAlreadyShown, userModeEnabled, expirationWarningShown);
        }
    }

    @Data
    public class Settings {
        /**
         * Allow analytics cookies or not. Should never be private.
         */
        private boolean analyticsCookiesAllowed = false;

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Settings settings = (Settings) o;
            return analyticsCookiesAllowed == settings.analyticsCookiesAllowed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(analyticsCookiesAllowed);
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
     * Updates current Session Version.
     *
     * @return same session to be able to use it in Streams.
     */
    public YalseeSession updateVersion() {
        final String versionString = new SimpleDateFormat(VERSION_FORMAT).format(AppUtils.now());
        version = Long.parseLong(versionString);
        return this;
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

    /**
     * Opposite of {@link #equals(Object)} to improve readability.
     *
     * @param o another {@link YalseeSession} to compare with.
     * @return true if sessions differ from each other, false if same.
     */
    public boolean differsFrom(final YalseeSession o) {
        return !equals(o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof YalseeSession)) return false;
        YalseeSession session = (YalseeSession) o;
        return getSessionId().equals(session.getSessionId())
                && Objects.equals(getDevice(), session.getDevice())
                && getFlags().equals(session.getFlags())
                && getSettings().equals(session.getSettings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSessionId(), getDevice(), getFlags(), getSettings(),
                getCreated(), getNotValidAfter(), getVersion());
    }

    private int sessionTimeout() {
        return AppUtils.getSessionTimeoutFromStaticContext();
    }

}
