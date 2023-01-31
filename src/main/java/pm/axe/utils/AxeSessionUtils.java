package pm.axe.utils;

import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.UserSettingsService;
import pm.axe.session.AxeSession;
import pm.axe.users.LandingPage;

import java.util.Optional;

/**
 * Utils class, that bound within current {@link com.vaadin.flow.spring.annotation.UIScope}.
 */
@RequiredArgsConstructor
@Component
@UIScope
public class AxeSessionUtils {
    private final UserSettingsService uss;

    /**
     * Shortcut for getting {@link UserSettings}.
     *
     * @return provides {@link UserSettings} of {@link User} bound to current {@link AxeSession}, if any.
     * or {@link Optional#empty()}
     */
    public Optional<UserSettings> getCurrentUserSettings() {
        Optional<AxeSession> axs = AxeSession.getCurrent();
        if (axs.isPresent() && axs.get().hasUser()) {
            return uss.getUserSettings(axs.get().getUser());
        }  else {
            return Optional.empty();
        }
    }

    public User boundUserIfAny() {
        Optional<AxeSession> axeSession = AxeSession.getCurrent();
        if (axeSession.isPresent()) {
            if (axeSession.get().hasUser()) {
                return axeSession.get().getUser();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public LandingPage getLandingPage() {
        final User user = this.boundUserIfAny();
        final Optional<UserSettings> us = this.getCurrentUserSettings();
        return (user != null && us.isPresent()) ? us.get().getLandingPage() : LandingPage.HOME_PAGE;
    }
}
