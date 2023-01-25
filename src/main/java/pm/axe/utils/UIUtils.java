package pm.axe.utils;

import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.UserSettingsService;
import pm.axe.session.AxeSession;

import java.util.Optional;

/**
 * Utils class, that bound within current {@link com.vaadin.flow.spring.annotation.UIScope}.
 */
@RequiredArgsConstructor
@Component
@UIScope
public class UIUtils {
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
}
