package pm.axe.ui.pages.settings;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.UserSettingsService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.Section;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AxeSessionUtils;

import java.util.Optional;

import static pm.axe.Axe.C.ONE_SECOND_IN_MILLIS;

@SpringComponent
@UIScope
@CssImport("./css/common_styles.css")
@Route(value = Endpoint.UI.SETTINGS_PAGE, layout = MainView.class)
@PageTitle("Settings - Axe.pm")
public class SettingsPage extends AxeBaseLayout implements BeforeEnterObserver {

    private final H3 pageTitle = new H3("Application Settings");
    private final Section cookieSettingsSection = new Section("Cookie Settings");
    private final Span techCookiesSpan = new Span();
    private final Span techCookiesLabel = new Span("Technical cookies: ");
    private final ToggleButton techCookiesValue = new ToggleButton(true);
    private final Span analyticsCookiesSpan = new Span();
    private final Span analyticsCookiesLabel = new Span("Analytics cookies: ");
    private final ToggleButton analyticsCookiesValue = new ToggleButton();
    private final Section betaSettingsSection = new Section("Beta (Feature preview)");
    private final Span darkModeSpan = new Span();
    private final Span darkModeLabel = new Span("Dark Mode: ");
    private final ToggleButton darkModeValue = new ToggleButton();

    private final Span usersFeatureSpan = new Span();
    private final Span usersFeatureLabel = new Span("Users: ");
    private final ToggleButton usersFeatureValue = new ToggleButton();
    private final Notification savedNotification = makeSavedNotification();

    private final MainView mainView;
    private final AxeSessionUtils axeSessionUtils;
    private final UserSettingsService uss;

    private boolean isClientChange;
    private UI currentUI;

    /**
     * Creates {@link SettingsPage}.
     *
     * @param mainView            main view bean to switch theme.
     * @param axeSessionUtils             session utils to read user settings from, if any user logged in.
     * @param uss to save updated {@link UserSettings}.
     */
    public SettingsPage(final MainView mainView, final AxeSessionUtils axeSessionUtils, final UserSettingsService uss) {
        this.mainView = mainView;
        this.axeSessionUtils = axeSessionUtils;
        this.uss = uss;
        pageInit();
    }

    /**
     * Trigger actions, when Dark Mode Toggle value changes. It applies dark mode,
     * updates dark mode setting in {@link AxeSession} and {@link UserSettings}.
     *
     * @param event event, that holder that has toggle value.
     */
    public void onDarkModeChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> event) {
        final boolean isDarkTheme = event.getValue();
        mainView.applyTheme(isDarkTheme);
        AxeSession.getCurrent().ifPresent(session -> session.getSettings().setDarkMode(event.getValue()));
        //write to user settings as well,  if any bound.
        axeSessionUtils.getCurrentUserSettings().ifPresent(us -> {
            us.setDarkMode(event.getValue());
            uss.updateUserSettings(us);
        });
    }

    private void pageInit() {
        setIds();
        setPageStructure();
        setInitialState();
        currentUI = UI.getCurrent();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        //automatic starts changing values - disabling notifications
        isClientChange = false;
        AxeSession.getCurrent()
                .ifPresent(ys -> {
                    analyticsCookiesValue.setValue(ys.getSettings().isAnalyticsCookiesAllowed());
                    darkModeValue.setValue(ys.getSettings().isDarkMode());
                    usersFeatureValue.setValue(ys.getSettings().isUsersFeatureEnabled());
                    if (ys.hasDevice()) adjustNotificationPosition(ys.getDevice().isMobile());
                });
        //set values from UserSettings
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        userSettings.ifPresent(us -> darkModeValue.setValue(us.isDarkMode()));
        //automatic set all values, all other actions are from client.
        isClientChange = true;
    }

    private void setIds() {
        setId(IDs.PAGE_ID);
        pageTitle.setId(IDs.PAGE_TITLE);
        cookieSettingsSection.setId(IDs.COOKIE_SETTINGS_TITLE);
        techCookiesSpan.setId(IDs.TECH_COOKIE_SPAN);
        techCookiesLabel.setId(IDs.TECH_COOKIE_LABEL);
        techCookiesValue.setId(IDs.TECH_COOKIE_VALUE);
        analyticsCookiesSpan.setId(IDs.ANALYTICS_COOKIE_SPAN);
        analyticsCookiesLabel.setId(IDs.ANALYTICS_COOKIE_LABEL);
        analyticsCookiesValue.setId(IDs.ANALYTICS_COOKIE_VALUE);

        betaSettingsSection.setId(IDs.BETA_SETTINGS_TITLE);
        darkModeSpan.setId(IDs.DARK_MODE_SPAN);
        darkModeLabel.setId(IDs.DARK_MODE_LABEL);
        darkModeValue.setId(IDs.DARK_MODE_VALUE);
    }

    private void setPageStructure() {
        techCookiesSpan.add(techCookiesLabel, techCookiesValue);
        analyticsCookiesSpan.add(analyticsCookiesLabel, analyticsCookiesValue);

        darkModeSpan.add(darkModeLabel, darkModeValue);
        usersFeatureSpan.add(usersFeatureLabel, usersFeatureValue, pageReloadPostfix());

        cookieSettingsSection.setContent(techCookiesSpan, analyticsCookiesSpan);
        betaSettingsSection.setContent(darkModeSpan, usersFeatureSpan);

        add(pageTitle, cookieSettingsSection, betaSettingsSection);
    }

    private void setInitialState() {
        techCookiesValue.setValue(true);
        techCookiesValue.setEnabled(false);
        analyticsCookiesValue.setValue(true);
        analyticsCookiesValue.addValueChangeListener(this::onAnalyticCookiesChanged);
        darkModeValue.setValue(false);
        darkModeValue.addValueChangeListener(this::onDarkModeChanged);
        usersFeatureValue.setValue(false);
        usersFeatureValue.addValueChangeListener(this::onUsersFeatureSwitchChanged);
    }

    private void onAnalyticCookiesChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> event) {
        AxeSession.getCurrent()
                .ifPresent(session -> session.getSettings().setAnalyticsCookiesAllowed(event.getValue()));
        mainView.getPiwikStatsBanner().optOut(!event.getValue());
        notifyClient();
    }

    private void onUsersFeatureSwitchChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> e) {
        AxeSession.getCurrent().ifPresent(session -> session.getSettings().setUsersFeatureEnabled(e.getValue()));
        notifyClient();
    }

    private void notifyClient() {
        if (isClientChange) {
            this.savedNotification.open();
        }
    }

    private Notification makeSavedNotification() {
        Notification notification = new Notification("Saved");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(ONE_SECOND_IN_MILLIS); //1 second
        return notification;
    }

    private void adjustNotificationPosition(final boolean isMobile) {
        Notification.Position position = isMobile ? Notification.Position.BOTTOM_CENTER : Notification.Position.MIDDLE;
        this.savedNotification.setPosition(position);
    }

    private Span pageReloadPostfix() {
        Span postfixSpan = new Span();
        postfixSpan.setClassName(Classes.POSTFIX);

        Span start = new Span(" (requires ");

        Button pageReloadButton = new Button("Page Reload");
        pageReloadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        pageReloadButton.setClassName(Classes.PAGE_RELOAD_BUTTON);
        pageReloadButton.addClickListener(e -> this.currentUI.getPage().reload());

        Span end = new Span(")");

        postfixSpan.add(start, pageReloadButton, end);
        return postfixSpan;
    }

    public static class IDs {
        public static final String PAGE_ID = "settingsPage";
        public static final String PAGE_TITLE = "pageTitle";
        public static final String COOKIE_SETTINGS_TITLE = "cookieSettingsTitle";
        public static final String TECH_COOKIE_SPAN = "techCookieSpan";
        public static final String TECH_COOKIE_LABEL = "techCookieLabel";
        public static final String TECH_COOKIE_VALUE = "techCookieValue";
        public static final String ANALYTICS_COOKIE_SPAN = "analyticsCookieSpan";
        public static final String ANALYTICS_COOKIE_LABEL = "analyticsCookieLabel";
        public static final String ANALYTICS_COOKIE_VALUE = "analyticsCookieValue";
        public static final String BETA_SETTINGS_TITLE = "betaSettingsTitle";
        public static final String DARK_MODE_SPAN = "darkModeSpan";
        public static final String DARK_MODE_LABEL = "darkModeLabel";
        public static final String DARK_MODE_VALUE = "darkModeValue";
    }

    public static class Classes {
        public static final String POSTFIX = "postfix-span";
        public static final String PAGE_RELOAD_BUTTON = "page-reload-button";
    }
}
