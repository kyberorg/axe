package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.server.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.redis.YalseeSession;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.YalseeSessionService;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.ui.components.CookieBanner;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.session.SessionBox;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.kyberorg.yalsee.ui.MainView.IDs.APP_LOGO;

@Slf4j
@SpringComponent
@UIScope
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(
        name = "Yalsee - the link shortener",
        shortName = "yalsee",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png", "Pebble-Regular.woff"},
        description = "Yalsee - the link shortener")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport("./css/main_view.css")
public class MainView extends AppLayout implements BeforeEnterObserver, PageConfigurator {
    private static final String TAG = "[" + MainView.class.getSimpleName() + "]";

    private final AppUtils appUtils;
    private final YalseeSessionService yalseeSessionService;

    private YalseeSession yalseeSession;

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabToTarget = new HashMap<>();

    /**
     * Creates Main Application (NavBar, Menu and Content) View.
     *
     * @param appUtils             application utils for determine dev mode
     * @param yalseeSessionService service for manipulating with {@link YalseeSession}.
     */
    public MainView(final AppUtils appUtils, YalseeSessionService yalseeSessionService) {
        this.appUtils = appUtils;
        this.yalseeSessionService = yalseeSessionService;

        init();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(tabToTarget.get(beforeEnterEvent.getNavigationTarget()));

        VaadinSession session = VaadinSession.getCurrent();
        //Cookie Banner
        readAndWriteCookieBannerRelatedSettingsFromSession(session);

        //TODO write it to UserSession
        //yalseeSession.setValue(App.Session.COOKIE_BANNER_ALREADY_SHOWN, true);
        //TODO replace it

        boolean bannerAlreadyShown = (boolean) session.getAttribute(App.Session.COOKIE_BANNER_ALREADY_SHOWN);
        if (!bannerAlreadyShown) {
            CookieBanner cookieBanner = new CookieBanner();
            cookieBanner.getContent().open();
            session.setAttribute(App.Session.COOKIE_BANNER_ALREADY_SHOWN, true);
        }
    }

    private void init() {
        //yalseeSession = getUserSession();

        setPrimarySection(Section.NAVBAR);

        //do not set touch-optimized to true, because it moves navbar down.
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

        //session trick
        VaadinSession session = VaadinSession.getCurrent();
        SessionBox.storeSession(session);

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

        layout.add(toggle, title);
        return layout;
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

    @SuppressWarnings("SameParameterValue") //currently, there is only single use, but more might be in the future.
    private void addMenuTab(final String label, final String targetUrl, final VaadinIcon icon) {
        Icon iconElement = icon.create();
        Span labelElement = new Span(label);
        Anchor link = new Anchor(targetUrl, iconElement, labelElement);
        Tab tab = new Tab(link);
        tabs.add(tab);
    }

    private YalseeSession getUserSession() {
        YalseeSession yalseeSession;
        Device currentDevice = getCurrentDevice();
        if (isFirstVisit()) {
            //create UserSession + save it to redis
            yalseeSession = createNewSession(currentDevice);
            sendSessionCookie(yalseeSession);
        } else {
            //read from cookie
            Cookie sessionCookie = appUtils.getCookieByName(App.CookieNames.USER_SESSION_COOKIE,
                    VaadinService.getCurrentRequest());
            OperationResult checkResult = yalseeSessionService.checkCookie(sessionCookie, currentDevice);
            if (checkResult.ok()) {
                yalseeSession = checkResult.getPayload(YalseeSession.class);
            } else {
                //something wrong with current session - override it
                log.warn("{} Session cookie check failed. Reason: {}", TAG, checkResult);
                yalseeSession = createNewSession(currentDevice);
                sendSessionCookie(yalseeSession);
            }
        }
        return yalseeSession;
    }

    private boolean isFirstVisit() {
        Cookie sessionCookie = appUtils.getCookieByName(App.CookieNames.USER_SESSION_COOKIE,
                VaadinService.getCurrentRequest());
        return Objects.isNull(sessionCookie);
    }

    private YalseeSession createNewSession(final Device device) {
        return yalseeSessionService.createNew(device);
    }

    private void sendSessionCookie(final YalseeSession yalseeSession) {
        //create cookie + remove current if any + send new to browser
        Cookie sessionCookie = yalseeSessionService.createCookie(yalseeSession);
        //resetting current if any
        Cookie resetCookie = new Cookie(sessionCookie.getName(), "-");
        resetCookie.setMaxAge(0);
        VaadinService.getCurrentResponse().addCookie(resetCookie);
        //sending normal one
        VaadinService.getCurrentResponse().addCookie(sessionCookie);
    }

    private Device getCurrentDevice() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        WebBrowser browser = VaadinSession.getCurrent().getBrowser();
        return Device.from(request, browser);
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
        if (appUtils.isGoogleAnalyticsEnabled() && appUtils.isGoogleAnalyticsAllowed(VaadinSession.getCurrent())) {
            settings.addInlineFromFile(appUtils.getGoggleAnalyticsFileName(), InitialPageSettings.WrapMode.NONE);
        }
    }

    private void readAndWriteCookieBannerRelatedSettingsFromSession(final VaadinSession session) {
        if (Objects.isNull(session.getAttribute(App.Session.COOKIE_BANNER_ALREADY_SHOWN))) {
            session.setAttribute(App.Session.COOKIE_BANNER_ALREADY_SHOWN, false);
        }

        if (Objects.isNull(session.getAttribute(App.Session.COOKIE_BANNER_ANALYTICS_ALLOWED))) {
            session.setAttribute(App.Session.COOKIE_BANNER_ANALYTICS_ALLOWED, false);
        }
    }

    public static class IDs {
        public static final String VIEW_ID = "mainView";
        public static final String APP_LOGO = "appLogo";
    }
}
