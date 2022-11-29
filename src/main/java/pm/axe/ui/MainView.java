package pm.axe.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import pm.axe.constants.App;
import pm.axe.events.session.AxeSessionAlmostExpiredEvent;
import pm.axe.events.session.AxeSessionDestroyedEvent;
import pm.axe.internal.Piwik;
import pm.axe.result.OperationResult;
import pm.axe.services.AxeSessionCookieService;
import pm.axe.services.AxeSessionService;
import pm.axe.session.AxeSession;
import pm.axe.session.Device;
import pm.axe.ui.elements.AppMenu;
import pm.axe.ui.elements.CookieBanner;
import pm.axe.ui.elements.PiwikStats;
import pm.axe.ui.elements.ProjektRenamedNotification;
import pm.axe.ui.pages.appinfo.AppInfoPage;
import pm.axe.ui.pages.debug.DebugPage;
import pm.axe.ui.pages.home.HomePage;
import pm.axe.ui.pages.meetaxe.MeetAxePage;
import pm.axe.ui.pages.mylinks.MyLinksPage;
import pm.axe.ui.pages.settings.SettingsPage;
import pm.axe.utils.AppUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static pm.axe.ui.MainView.IDs.APP_LOGO;
@Slf4j
@SpringComponent
@UIScope
@CssImport("./css/main_view.css")
@JsModule("./js/open-share-menu.js")
@JsModule("./js/piwik.js")
public class MainView extends AppLayout implements BeforeEnterObserver {
    private static final String TAG = "[" + MainView.class.getSimpleName() + "]";

    private final AppUtils appUtils;
    private final AxeSessionService sessionService;
    private final AxeSessionCookieService cookieService;
    private final Piwik piwikConfig;

    private HorizontalLayout header;
    private final Component appMenuPlaceholder = new Button();
    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabToTarget = new HashMap<>();

    private final ProjektRenamedNotification projektRenamedNotification = ProjektRenamedNotification.create();

    private final FlexLayout announcementLine = new FlexLayout();

    @Getter
    private PiwikStats piwikStats;
    @Getter
    private final UI ui = UI.getCurrent();
    private final Device currentDevice;
    private String currentSessionId;

    //needed to prevent execution of {@link #beforeEnter()} twice;
    private boolean pageAlreadyInitialized = false;

    /**
     * Creates Main Application (NavBar, Menu and Content) View.
     *
     * @param appUtils       application utils for determine dev mode
     * @param sessionService service for manipulating with {@link AxeSession}.
     * @param cookieService  service for actions with {@link AxeSession} {@link Cookie}.
     * @param piwikConfig bean with Piwik configuration.
     */
    public MainView(final AppUtils appUtils,
                    final AxeSessionService sessionService, final AxeSessionCookieService cookieService,
                    final Piwik piwikConfig) {
        this.appUtils = appUtils;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.piwikConfig = piwikConfig;

        this.currentDevice = getCurrentDevice();
        init();
    }

    @PostConstruct
    private void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    private void init() {
        //session init
        AxeSession session = getAxeSession();
        AxeSession.setCurrent(session);

        this.currentSessionId = Objects.nonNull(session)
                ? session.getSessionId() : AxeSession.NO_SESSION_STORED_MARKER;

        if (session != null) {
            applyTheme(session.getSettings().isDarkMode());
        }

        setPrimarySection(Section.NAVBAR);

        //do not set touch-optimized to true, because it moves navbar down.
        header = createHeader();
        addToNavbar(header);

        //items
        addLogo();
        addSubTitle();
        addMenuTab("Main", HomePage.class, VaadinIcon.HOME);
        addMenuTab("My Links", MyLinksPage.class, VaadinIcon.TABLE);
        addMenuTab("App Info", AppInfoPage.class, VaadinIcon.INFO);
        addMenuTab("Settings", SettingsPage.class, VaadinIcon.COG);
        addMenuTab("API Doks", App.Api.API_DOKS_URL, VaadinIcon.CURLY_BRACKETS);
        addMenuTab("Meet Axe", MeetAxePage.class, VaadinIcon.COMMENT_ELLIPSIS);

        // dev-only items
        if (appUtils.isDevelopmentModeActivated()) {
            addMenuTab("Debug", DebugPage.class, VaadinIcon.FLASK);
        }

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);

        //default visual state for announcement line
        announcementLine.removeAll();
        announcementLine.setVisible(false);

        setId(IDs.VIEW_ID);

        // hide the splash screen after the main view is loaded
        UI.getCurrent().getPage().executeJs(
                "document.querySelector('#splash-screen').classList.add('loaded')");
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        //this one line should be executed every time - even if page already initialized.
        tabs.setSelectedTab(tabToTarget.get(beforeEnterEvent.getNavigationTarget()));
        //avoid double init
        if (pageAlreadyInitialized) return;

        Optional<AxeSession> session = AxeSession.getCurrent();
        //Cookie Banner
        boolean bannerAlreadyShown;
        bannerAlreadyShown = session.map(ys -> ys.getFlags().isCookieBannerAlreadyShown()).orElse(true);

        if (!bannerAlreadyShown) {
            CookieBanner cookieBanner = new CookieBanner();
            cookieBanner.getContent().open();
            session.ifPresent(ys -> ys.getFlags().setCookieBannerAlreadyShown(true));
        }

        //Rename notification
        boolean isRenameNotificationEnabled = appUtils.showRenameNotification();
        boolean isFromYalsee = StringUtils.isNotBlank(VaadinRequest.getCurrent().getParameter("yalsee"));
        boolean renameNotificationAlreadyShown = session.map(ys -> ys.getFlags().isRenameNotificationAlreadyShown())
                .orElse(false);

        if (isRenameNotificationEnabled && isFromYalsee && !renameNotificationAlreadyShown) {
            //show - new name notification.
            projektRenamedNotification.show();
        }

        boolean isUserFeatureEnabled;
        isUserFeatureEnabled = session.map(ys -> ys.getSettings().isUsersFeatureEnabled()).orElse(false);
        if (isUserFeatureEnabled) {
            AppMenu appMenu = AppMenu.createNormalMenu();
            header.replace(appMenuPlaceholder, appMenu);
            appMenu.moveUserButtonToFarRight();
        }

        //create Piwik Statistics
        boolean piwikEnabled = piwikConfig.isEnabled();
        boolean analyticsCookieAllowed =
                session.map(as -> as.getSettings().isAnalyticsCookiesAllowed()).orElse(true);
        boolean showAnnouncement =
                session.map(as -> as.getFlags().showAnnouncement()).orElse(true);

        piwikStats = new PiwikStats(piwikConfig, this);
        if (piwikEnabled && analyticsCookieAllowed) {
            //addPiwikElement();
            piwikStats.enableStats();
        }

        if (piwikStats.isNotEmpty() && showAnnouncement) {
            addAnnouncement(piwikStats);
        }

        pageAlreadyInitialized = true;
    }

    /**
     * Closes (hides) and clears announcement line up.
     * This method also updates {@link AxeSession} to prevent showing announcement again and again.
     */
    public void closeAnnouncementLine() {
        announcementLine.setVisible(false);
        announcementLine.removeAll();
        AxeSession.getCurrent().ifPresent(as -> as.getFlags().setDontShowAnnouncement(true));
    }

    /**
     * Refreshes page when {@link #currentSessionId} is destroyed. After refresh client should get new session.
     *
     * @param event event, which indicates that session is destroyed.
     */
    @Subscribe
    public void onSessionDeleted(final AxeSessionDestroyedEvent event) {
        AxeSession destroyedSession = event.getAxeSession();
        if (currentSessionId.equals(destroyedSession.getSessionId())) {
            refreshPage();
        }
    }

    /**
     * Shows warning when {@link #currentSessionId} expires within or less then
     * {@link AxeSession#TIMEOUT_FOR_WARNING_MINUTES}.
     *
     * @param event event, which informs about session expiry.
     */
    @Subscribe
    public void showWarningOnAlmostExpiredSession(final AxeSessionAlmostExpiredEvent event) {
        AxeSession session = event.getAxeSession();
        if (session == null) return;
        if (currentSessionId.equals(session.getSessionId())) {
            showSessionExpiryWarning(session);
            session.getFlags().setExpirationWarningShown(true);
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        DrawerToggle toggle = new DrawerToggle();

        String siteTitle = appUtils.getEnv().getProperty(App.Properties.APP_SITE_TITLE, "axe").toUpperCase();
        Span title = new Span(siteTitle);
        title.addClassName("site-title");

        Span testName = new Span();
        testName.setId(IDs.TEST_NAME_SPAN);

        appMenuPlaceholder.setVisible(false);

        layout.add(toggle, title, testName, appMenuPlaceholder);
        return layout;
    }

    private void addLogo() {
        Image logo = new Image("/images/logo_long.png", "AxeLogo");
        logo.setId(APP_LOGO);
        logo.addClassName("logo-image");

        RouterLink logoLink = new RouterLink("", HomePage.class);
        logoLink.add(logo);

        Tab logoTab = new Tab(logoLink);
        tabs.add(logoTab);
    }

    private void addSubTitle() {
        Tab subTitleTab = new Tab("Axe.pm - Short Links for free");
        subTitleTab.setEnabled(false);
        subTitleTab.addClassName("subtitle-tab");
        tabs.add(subTitleTab);
    }

    private void addMenuTab(final String label, final Class<? extends Component> target, final VaadinIcon icon) {
        RouterLink link = new RouterLink("", target);
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

    private void addAnnouncement(final Component announcement) {
        announcementLine.removeAll();
        announcementLine.setId("axeAnnouncement");
        announcementLine.setClassName("axe-announcement-line");
        announcementLine.add(announcement);
        getElement().appendChild(announcementLine.getElement());
        announcementLine.setVisible(true);
    }

    private AxeSession getAxeSession() {
        AxeSession axeSession;
        if (isFirstVisit()) {
            if (currentDevice.isRobot()) return null;
            if (currentDevice.isInternalTraffic()) return null;
            //create UserSession + save it to redis
            axeSession = createNewSession(currentDevice);
            sendSessionCookie(axeSession);
        } else {
            //read from cookie
            Cookie sessionCookie = appUtils.getCookieByName(App.CookieNames.AXE_SESSION,
                    VaadinService.getCurrentRequest());
            OperationResult checkResult = cookieService.checkCookie(sessionCookie, currentDevice);
            if (checkResult.ok()) {
                axeSession = checkResult.getPayload(AxeSession.class);
            } else {
                log.warn("{} Session cookie check failed. Reason: {}", TAG, checkResult);
                axeSession = createNewSession(currentDevice);
                sendSessionCookie(axeSession);
            }
        }
        return axeSession;
    }

    private boolean isFirstVisit() {
        Cookie sessionCookie = appUtils.getCookieByName(App.CookieNames.AXE_SESSION,
                VaadinService.getCurrentRequest());
        return Objects.isNull(sessionCookie);
    }

    private AxeSession createNewSession(final Device device) {
        return sessionService.createNew(device);
    }

    private void sendSessionCookie(final AxeSession axeSession) {
        //create cookie + remove current if any + send new to browser
        Cookie sessionCookie = cookieService.createCookie(axeSession);
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

    private void showSessionExpiryWarning(final AxeSession session) {
        this.ui.access(() -> AppUtils.getSessionExpiredNotification(this.ui, session).open());
    }

    private void refreshPage() {
        this.ui.access(() -> this.ui.getPage().reload());
    }

    /**
     * Applies theme.
     *
     * @param isDarkTheme shall Dark theme be applied instead of default one.
     */
    public void applyTheme(final boolean isDarkTheme) {
        final String theme = isDarkTheme ? "dark" : "light";
        this.ui.getPage().executeJs("document.documentElement.setAttribute(\"theme\",\"" + theme + "\")");
    }

    @PreDestroy
    private void unregister() {
        EventBus.getDefault().unregister(this);
    }

    public static class IDs {
        public static final String VIEW_ID = "mainView";
        public static final String APP_LOGO = "appLogo";
        public static final String TEST_NAME_SPAN = "testName";
    }
}
