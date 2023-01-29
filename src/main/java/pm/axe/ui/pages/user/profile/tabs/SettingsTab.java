package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
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
import pm.axe.ui.elements.Section;
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

    private Section loginSessionSection;
    private Section landingPageSection;
    private Section darkModeSection;

    private final Select<String> landingPageSelect = new Select<>();
    private final ToggleButton darkModeToggle = new ToggleButton();
    @Override
    public void tabInit(final User user) {
        removeAll();
        loginSessionSection = createLoginSessionSection();
        landingPageSection = createLandingPageSection();
        darkModeSection = createDarkModeSection();

        add(loginSessionSection, landingPageSection, darkModeSection);
    }

    private Section createLoginSessionSection() {
        loginSessionSection = new Section("Login Session Duration");
        Component content = loginSessionDuration();
        loginSessionSection.setContent(content);
        return loginSessionSection;
    }

    private Component loginSessionDuration() {
        //TODO may be a single component
        IntegerField amountField = new IntegerField();
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

        FlexLayout fields = new FlexLayout(amountField, unitSelect, save);
        fields.setAlignItems(Alignment.BASELINE);
        fields.setMaxHeight(100, Unit.PERCENTAGE);

        Span explanationSpan = new Span("Time you will stay logged in after successful login");

        VerticalLayout content = new VerticalLayout(fields, explanationSpan);
        content.setPadding(false);
        return content;
    }

    private Section createLandingPageSection() {
        landingPageSection = new Section("Landing page");
        Component content = landingPageLayout();
        landingPageSection.setContent(content);
        return landingPageSection;
    }

    private Component landingPageLayout() {
        landingPageSelect.setLabel("Landing Page");
        landingPageSelect.setItems(Arrays.stream(LandingPage.values()).map(LandingPage::name).toList());
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        userSettings.ifPresent(settings -> landingPageSelect.setValue(settings.getLandingPage().name()));
        landingPageSelect.addValueChangeListener(this::onLandingPageSelectChanged);

        Span explanationSpan = new Span("Page that opens after login");
        VerticalLayout content = new VerticalLayout(landingPageSelect, explanationSpan);
        content.setPadding(false);
        return content;
    }

    private Section createDarkModeSection() {
        darkModeSection = new Section("Dark Mode");
        Component content = darkModeLayout();
        darkModeSection.setContent(content);
        return darkModeSection;
    }

    private Component darkModeLayout() {
        Span offSpan = new Span("Off");

        darkModeToggle.addValueChangeListener(this::onDarkModeToggleChanged);
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        userSettings.ifPresent(us -> darkModeToggle.setValue(us.isDarkMode()));
        darkModeToggle.addClassName("toggle-with-prefix-postfix");

        Span onSpan = new Span("On");

        FlexLayout layout = new FlexLayout(offSpan, darkModeToggle, onSpan);
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
