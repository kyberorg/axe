package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.ui.dev.AppInfoView;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.session.SessionBox;

import java.util.HashMap;
import java.util.Map;

import static io.kyberorg.yalsee.ui.MainView.IDs.APP_LOGO;
import static io.kyberorg.yalsee.ui.MainView.IDs.USER_MENU;

@SpringComponent
@UIScope
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(
        name = "Yalsee - the link shortener",
        shortName = "yalsee",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png"},
        description = "Yalsee - the link shortener")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport("./css/main_view.css")
public class MainView extends AppLayout implements BeforeEnterObserver, PageConfigurator {

    private final AppUtils appUtils;

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabToTarget = new HashMap<>();

    private Button loginButton;
    private Button registerButton;

    /**
     * Creates Main Application (NavBar, Menu and Content) View.
     *
     * @param appUtils application utils for determine dev mode
     */
    public MainView(final AppUtils appUtils) {
        this.appUtils = appUtils;

        setPrimarySection(Section.NAVBAR);
        addToNavbar(createHeader());

        //items
        addLogo();
        addSubTitle();
        addMenuTab("Main", HomeView.class, VaadinIcon.HOME);
        addMenuTab("My Links", MyLinksView.class, VaadinIcon.TABLE);
        addMenuTab("App Info", AppInfoView.class, VaadinIcon.INFO);
        addMenuTab("API Doks", App.Api.API_DOKS_URL, VaadinIcon.CURLY_BRACKETS);

        // dev-only items
        if (appUtils.isDevelopmentModeActivated()) {
            addMenuTab("Debug", DebugView.class, VaadinIcon.FLASK);
        }

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);

        setId(IDs.VIEW_ID);

        //session tricks
        SessionBox.storeSession(VaadinSession.getCurrent());

        // hide the splash screen after the main view is loaded
        UI.getCurrent().getPage().executeJs(
                "document.querySelector('#splash-screen').classList.add('loaded')");
    }

    private Component createHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        DrawerToggle toggle = new DrawerToggle();

        String siteTitle = appUtils.getEnv().getProperty(App.Properties.APP_SITE_TITLE, "yalsee").toUpperCase();
        Span title = new Span(siteTitle);
        title.addClassName("site-title");

        MenuBar userButton = userMenu();
        userButton.setId(IDs.USER_BUTTON);
        userButton.getStyle().set("margin-left", "auto");
        userButton.getStyle().set("margin-right", "1rem");

        layout.add(toggle, title, userButton);
        return layout;
    }

    private MenuBar userMenu() {
        MenuBar userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem profile = userMenu.addItem(VaadinIcon.USER.create());
        profile.setId(USER_MENU);

        HorizontalLayout buttons = new HorizontalLayout();

        loginButton = new Button("Log in");
        loginButton.setId(IDs.LOGIN_BUTTON);
        loginButton.addClickListener(this::onLoginButtonClicked);

        registerButton = new Button("Register");
        registerButton.setId(IDs.REGISTER_BUTTON);
        registerButton.addClickListener(this::onRegisterButtonClicked);

        buttons.add(loginButton, registerButton);

        profile.getSubMenu().addItem(buttons);
        //line
        profile.getSubMenu().add(new Hr());

        //why register and terms
        profile.getSubMenu().addItem("Why register?", this::showWhyRegisterModal);
        profile.getSubMenu().addItem("Terms of service", this::showTermsOfServiceModal);

        return userMenu;
    }

    private void addLogo() {
        Image logo = new Image("/images/logo.png", "Icon");
        logo.setId(APP_LOGO);
        logo.addClassName("logo-image");
        Tab logoTab = new Tab(logo);
        logoTab.setEnabled(false);
        tabs.add(logoTab);
    }

    private void addSubTitle() {
        Tab subTitleTab = new Tab("Yalsee - the link shortener");
        subTitleTab.setEnabled(false);
        subTitleTab.addClassName("subtitle-tab");
        tabs.add(subTitleTab);
    }

    private void addMenuTab(final String label, final Class<? extends Component> target, final VaadinIcon icon) {
        RouterLink link = new RouterLink(null, target);
        link.add(icon.create());
        link.add(label);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        Tab tab = new Tab(link);
        tabToTarget.put(target, tab);
        tabs.add(tab);
    }

    @SuppressWarnings("SameParameterValue")
    private void addMenuTab(final String label, final String targetUrl, final VaadinIcon icon) {
        Icon iconElement = icon.create();
        Span labelElement = new Span(label);
        Anchor link = new Anchor(targetUrl, iconElement, labelElement);
        Tab tab = new Tab(link);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(tabToTarget.get(beforeEnterEvent.getNavigationTarget()));
    }

    @Override
    public void configurePage(final InitialPageSettings settings) {
        String title = "Yalsee - the link shortener";
        String description = "The free URL shortener for making long and ugly links into short URLs"
                + " that are easy to share and use.";

        String previewImage = appUtils.getServerUrl() + Endpoint.TNT.PREVIEW_IMAGE;

        settings.addFavIcon("icon", "/icons/favicon-32x32.png", "32x32");
        settings.addLink("shortcut icon", "/icons/favicon-16x16.png");
        settings.addLink("apple-touch-icon", "/icons/apple-touch-icon.png");
        settings.addLink("manifest", "/site.webmanifest");
        settings.addLink("mask-icon", "/icons/safari-pinned-tab.svg");

        settings.addMetaTag("apple-mobile-web-app-title", "yalsee");
        settings.addMetaTag("application-name", "yalsee");
        settings.addMetaTag("msapplication-TileColor", "#ffc40d");
        settings.addMetaTag("theme-color", "#ffffff");

        //SEO Tags
        settings.addMetaTag("title", title);
        settings.addMetaTag("description", description);

        settings.addMetaTag("og:type", "website");
        settings.addMetaTag("og:url", appUtils.getServerUrl());
        settings.addMetaTag("og:title", title);
        settings.addMetaTag("og:description", description);
        settings.addMetaTag("og:image", previewImage);

        settings.addMetaTag("twitter:card", "summary_large_image");
        settings.addMetaTag("twitter:url", appUtils.getServerUrl());
        settings.addMetaTag("twitter:title", title);
        settings.addMetaTag("twitter:description", description);
        settings.addMetaTag("twitter:image", previewImage);

        settings.addInlineFromFile("splash-screen.html", InitialPageSettings.WrapMode.NONE);
        if (appUtils.isGoogleAnalyticsEnabled()) {
            settings.addInlineFromFile(appUtils.getGoggleAnalyticsFileName(), InitialPageSettings.WrapMode.NONE);
        }
    }

    private void showWhyRegisterModal(ClickEvent<MenuItem> menuItemClickEvent) {
        Notification.show("Why Register Modal should be here").open();
    }

    private void showTermsOfServiceModal(ClickEvent<MenuItem> menuItemClickEvent) {
        Notification.show("Terms of Service Modal should be here").open();
    }

    private void onLoginButtonClicked(ClickEvent<Button> buttonClickEvent) {
        loginButton.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    }

    private void onRegisterButtonClicked(ClickEvent<Button> buttonClickEvent) {
        registerButton.getUI().ifPresent(ui -> ui.navigate(RegistrationView.class));
    }

    public static class IDs {
        public static final String VIEW_ID = "mainView";
        public static final String APP_LOGO = "appLogo";
        public static final String USER_BUTTON = "userButton";
        public static String USER_MENU = "userMenu";
        public static final String LOGIN_BUTTON = "loginButton";
        public static final String REGISTER_BUTTON = "registerButton";
    }
}
