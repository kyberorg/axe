package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.internal.HasTabInit;
import pm.axe.services.user.UserSettingsService;
import pm.axe.ui.pages.settings.SettingsPage;
import pm.axe.users.LandingPage;
import pm.axe.utils.AxeSessionUtils;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@SpringComponent
@UIScope
public class SettingsTab extends VerticalLayout implements HasTabInit {
    private final AxeSessionUtils axeSessionUtils;
    private final UserSettingsService uss;
    private final SettingsPage settingsPage;

    private final Select<String> landingPageSelect = new Select<>();
    private final ToggleButton darkModeToggle = new ToggleButton();
    @Override
    public void tabInit(final User user) {
        removeAll();
        add(landingPageLayout(), darkModeLayout(), loginSessionDuration());
    }

    private Component landingPageLayout() {
        landingPageSelect.setLabel("Landing Page");
        landingPageSelect.setItems(Arrays.stream(LandingPage.values()).map(LandingPage::name).toList());
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        userSettings.ifPresent(settings -> landingPageSelect.setValue(settings.getLandingPage().name()));
        landingPageSelect.addValueChangeListener(this::onLandingPageSelectChanged);
        //TODO add tooltip with manual trigger
        FlexLayout layout = new FlexLayout(landingPageSelect);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private Component darkModeLayout() {
        darkModeToggle.setLabel("Dark Mode: ");
        darkModeToggle.addValueChangeListener(this::onDarkModeToggleChanged);
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        userSettings.ifPresent(us -> darkModeToggle.setValue(us.isDarkMode()));

        FlexLayout layout = new FlexLayout(darkModeToggle);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private Component loginSessionDuration() {
        //TODO may be a single component
        IntegerField amountField = new IntegerField();
        amountField.setLabel("Login Session Duration");
        amountField.setStepButtonsVisible(true);
        amountField.setMin(1);
        amountField.setMax(60);
        amountField.setValue(1); //TODO replace with real value from settings
        Select<String> unitSelect = new Select<>();
        unitSelect.setItems(ChronoUnit.SECONDS.name(), ChronoUnit.MINUTES.name(),
                ChronoUnit.HOURS.name(), ChronoUnit.DAYS.name()); //TODO continue list
        unitSelect.setValue(ChronoUnit.DAYS.name());
        //TODO on unit change change amount field number max/min values
        Button save = new Button("Save");
        save.addClickListener(this::onSessionDurationSaved);

        FlexLayout layout = new FlexLayout(amountField, unitSelect, save);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private void onLandingPageSelectChanged(AbstractField.ComponentValueChangeEvent<Select<String>, String> event) {
        LandingPage landingPage = LandingPage.valueOf(landingPageSelect.getValue());
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        if (userSettings.isPresent()) {
            userSettings.get().setLandingPage(landingPage);
            uss.updateUserSettings(userSettings.get());
            Notification.show("Saved");
        }
    }

    private void onDarkModeToggleChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> event) {
        settingsPage.onDarkModeChanged(event); // do same action as in Settings Page
    }

    private void onSessionDurationSaved(ClickEvent<Button> event) {
        //TODO implement
        Notification.show("Not implemented yet");
    }
}
