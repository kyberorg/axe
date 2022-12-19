package pm.axe.session;

import com.google.gson.Gson;
import com.vaadin.flow.server.VaadinSession;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import pm.axe.db.models.User;
import pm.axe.services.AxeSessionService;
import pm.axe.utils.AppUtils;

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
public class AxeSession {
    /**
     * Default length of session id.
     */
    public static final int SESSION_ID_LEN = 40;
    public static final String NO_SESSION_STORED_MARKER = "DummySessionId";
    public static final int TIMEOUT_FOR_WARNING_MINUTES = 5;
    private static final String VERSION_FORMAT = "yyMMddHHmmssSSS";

    private final String sessionId = RandomStringUtils.randomAlphanumeric(SESSION_ID_LEN);
    private final Device device;
    private User user = User.createPseudoUser();
    private final Flags flags = new Flags();
    private final Settings settings = new Settings();

    private final Date created = AppUtils.now();
    private final Date notValidAfter = Date.from(Instant.now().plusSeconds(sessionTimeout()));
    private long version;

    @Data
    public static class Flags {
        /**
         * Defines is banner is already displayed and should not appear once again.
         */
        private boolean cookieBannerAlreadyShown = true;

        /**
         * Did we already warn user about session expiry ?
         */
        private boolean expirationWarningShown = false;

        /**
         * Defines if Rename Notification already shown.
         */
        private boolean renameNotificationAlreadyShown = false;

        /**
         * Defines that announcement is closed and should not be shown again.
         */
        private boolean dontShowAnnouncement = false;

        /**
         * Opposite of {@link #isDontShowAnnouncement()}.
         *
         * @return true - if announcement should be shown or false if not,
         */
        public boolean showAnnouncement() {
            return !dontShowAnnouncement;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Flags flags = (Flags) o;
            return cookieBannerAlreadyShown == flags.cookieBannerAlreadyShown
                    && expirationWarningShown == flags.expirationWarningShown
                    && renameNotificationAlreadyShown == flags.renameNotificationAlreadyShown
                    && dontShowAnnouncement == flags.dontShowAnnouncement;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cookieBannerAlreadyShown, expirationWarningShown);
        }
    }

    @Data
    public static class Settings {
        /**
         * Allow analytics cookies or not. Should never be private.
         */
        private boolean analyticsCookiesAllowed = true;

        /**
         * Is Dark Mode enabled or default one.
         */
        private boolean darkMode = false;

        /**
         * Is Users Feature enabled.
         */
        private boolean usersFeatureEnabled = false;

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Settings settings = (Settings) o;
            return analyticsCookiesAllowed == settings.analyticsCookiesAllowed
                    && darkMode == settings.darkMode
                    && usersFeatureEnabled == settings.usersFeatureEnabled;
        }

        @Override
        public int hashCode() {
            return Objects.hash(analyticsCookiesAllowed, darkMode, usersFeatureEnabled);
        }
    }

    /**
     * Stores given session to current {@link VaadinSession#getCurrent()} if it is available.
     *
     * @param session {@link AxeSession} to store.
     * @throws IllegalStateException when no {@link VaadinSession} available.
     */
    public static void setCurrent(final AxeSession session) {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            throw new IllegalStateException("No VaadinSession at this scope");
        }

        if (session == null) {
            vaadinSession.setAttribute(AxeSession.class.getSimpleName(), NO_SESSION_STORED_MARKER);
        } else {
            vaadinSession.setAttribute(AxeSession.class.getSimpleName(), session.getSessionId());
        }
    }

    /**
     * Gets the currently used session.
     * The current session is automatically defined where {@link VaadinSession#getCurrent()} is available.
     * In other cases, (e.g. from background threads), the current session is not automatically defined.
     *
     * @return the current session instance if available, otherwise {@link Optional#empty()}.
     */
    public static Optional<AxeSession> getCurrent() {
        final VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            return Optional.empty();
        }
        String sessionId = (String) VaadinSession.getCurrent().getAttribute(AxeSession.class.getSimpleName());
        if (StringUtils.isBlank(sessionId) || sessionId.equals(NO_SESSION_STORED_MARKER)) {
            return Optional.empty();
        }
        if (AxeSessionService.getInstance() != null) {
            return AxeSessionService.getInstance().getSession(sessionId);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creating empty {@link AxeSession}. Constructor for {@link Gson}.
     */
    public AxeSession() {
        this.device = null;
        this.updateVersion();
    }

    /**
     * Creates session for given {@link Device}.
     *
     * @param device device linked with {@link AxeSession}.
     */
    public AxeSession(final Device device) {
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
    public AxeSession updateVersion() {
        final String versionString = new SimpleDateFormat(VERSION_FORMAT).format(AppUtils.now());
        version = Long.parseLong(versionString);
        return this;
    }

    /**
     * Compares versions and calculates if current session is newer (has bigger version) then another.
     *
     * @param anotherSession another {@link AxeSession} to compare with.
     * @return true if current session is newer than compared one, false if not.
     */
    public boolean isNewer(final AxeSession anotherSession) {
        if (anotherSession == null || anotherSession.getVersion() == 0) return false;
        return this.getVersion() > anotherSession.getVersion();
    }

    /**
     * Compares versions and calculates if current session is older (has smaller version) then another.
     *
     * @param anotherSession another {@link AxeSession} to compare with.
     * @return true if current session is older than compared one, false if not.
     */
    public boolean isOlder(final AxeSession anotherSession) {
        if (anotherSession == null || anotherSession.getVersion() == 0) return false;
        return this.getVersion() < anotherSession.getVersion();
    }

    /**
     * Opposite of {@link #equals(Object)} to improve readability.
     *
     * @param o another {@link AxeSession} to compare with.
     * @return true if sessions differ from each other, false if same.
     */
    public boolean differsFrom(final AxeSession o) {
        return !equals(o);
    }

    /**
     * Report is session has {@link Device} object bound.
     *
     * @return true if {@link Device} is not {@code null}, false if it is.
     */
    public boolean hasDevice() {
        return device != null;
    }

    /**
     * If session has valid User id. Normally, this indicates that user logged in.
     *
     * @return true - if active user session bound to given {@link AxeSession}, false if not.
     */
    public boolean hasUser() {
        return getUser() != User.createPseudoUser();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AxeSession session)) return false;
        return getSessionId().equals(session.getSessionId())
                && Objects.equals(getUser(), session.getUser())
                && Objects.equals(getDevice(), session.getDevice())
                && getFlags().equals(session.getFlags())
                && getSettings().equals(session.getSettings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSessionId(), getUser(), getDevice(), getFlags(), getSettings(),
                getCreated(), getNotValidAfter(), getVersion());
    }

    private int sessionTimeout() {
        return AppUtils.getSessionTimeoutFromStaticContext();
    }
}
