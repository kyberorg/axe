package io.kyberorg.yalsee.ui;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import static io.kyberorg.yalsee.constants.App.ONE_SECOND_IN_MILLIS;

@SpringComponent
@UIScope
@CssImport("./css/common_styles.css")
@Route(value = Endpoint.UI.SETTINGS_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Settings Page")
public class SettingsPage extends YalseeLayout implements BeforeEnterObserver {

    private final H3 pageTitle = new H3("Application Settings");
    private final H4 cookieSettingsTitle = new H4("Cookie Settings");
    private final Span techCookiesSpan = new Span();
    private final Span techCookiesLabel = new Span("Technical cookies: ");
    private final ToggleButton techCookiesValue = new ToggleButton(true);
    private final Span analyticsCookiesSpan = new Span();
    private final Span analyticsCookiesLabel = new Span("Analytics cookies: ");
    private final ToggleButton analyticsCookiesValue = new ToggleButton();
    private final H4 betaSettingsTitle = new H4("Beta (Feature preview)");
    private final Span darkModeSpan = new Span();
    private final Span darkModeLabel = new Span("Dark Mode: ");
    private final ToggleButton darkModeValue = new ToggleButton();
    private final Notification savedNotification = makeSavedNotification();

    private final MainView mainView;

    private boolean isClientChange;
    private UI currentUI;

    /**
     * Creates {@link SettingsPage}.
     *
     * @param mainView main view bean to switch theme.
     */
    public SettingsPage(final MainView mainView) {
        this.mainView = mainView;
        pageInit();
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
        YalseeSession.getCurrent()
                .ifPresent(ys -> {
                    analyticsCookiesValue.setValue(ys.getSettings().isAnalyticsCookiesAllowed());
                    darkModeValue.setValue(ys.getSettings().isDarkMode());
                    if (ys.hasDevice()) adjustNotificationPosition(ys.getDevice().isMobile());
                });
        //automatic set all values, all other actions are from client.
        isClientChange = true;
    }

    private void setIds() {
        setId(IDs.PAGE_ID);
        pageTitle.setId(IDs.PAGE_TITLE);
        cookieSettingsTitle.setId(IDs.COOKIE_SETTINGS_TITLE);
        techCookiesSpan.setId(IDs.TECH_COOKIE_SPAN);
        techCookiesLabel.setId(IDs.TECH_COOKIE_LABEL);
        techCookiesValue.setId(IDs.TECH_COOKIE_VALUE);
        analyticsCookiesSpan.setId(IDs.ANALYTICS_COOKIE_SPAN);
        analyticsCookiesLabel.setId(IDs.ANALYTICS_COOKIE_LABEL);
        analyticsCookiesValue.setId(IDs.ANALYTICS_COOKIE_VALUE);

        betaSettingsTitle.setId(IDs.BETA_SETTINGS_TITLE);
        darkModeSpan.setId(IDs.DARK_MODE_SPAN);
        darkModeLabel.setId(IDs.DARK_MODE_LABEL);
        darkModeValue.setId(IDs.DARK_MODE_VALUE);
    }

    private void setPageStructure() {
        techCookiesSpan.add(techCookiesLabel, techCookiesValue);
        analyticsCookiesSpan.add(analyticsCookiesLabel, analyticsCookiesValue, pageReloadPostfix());

        darkModeSpan.add(darkModeLabel, darkModeValue);

        add(pageTitle, cookieSettingsTitle, techCookiesSpan, analyticsCookiesSpan, betaSettingsTitle, darkModeSpan);
    }

    private void setInitialState() {
        techCookiesValue.setValue(true);
        techCookiesValue.setEnabled(false);
        analyticsCookiesValue.setValue(true);
        analyticsCookiesValue.addValueChangeListener(this::onAnalyticCookiesChanged);
        darkModeValue.setValue(false);
        darkModeValue.addValueChangeListener(this::onDarkModeChanged);
    }

    private void onAnalyticCookiesChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> event) {
        YalseeSession.getCurrent()
                .ifPresent(session -> session.getSettings().setAnalyticsCookiesAllowed(event.getValue()));
        notifyClient();
    }

    private void onDarkModeChanged(final AbstractField.ComponentValueChangeEvent<ToggleButton, Boolean> event) {
        final boolean isDarkTheme = event.getValue();
        mainView.applyTheme(isDarkTheme);
        YalseeSession.getCurrent().ifPresent(session -> session.getSettings().setDarkMode(event.getValue()));
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