package eu.yals.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import eu.yals.ui.dev.AppInfoView;
import eu.yals.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

@SpringComponent
@UIScope
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(
        name = "Yet another link shortener",
        shortName = "yals",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png"},
        description = "Yet another link shortener for friends")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport("./css/main_view.css")
public class MainView extends AppLayout implements BeforeEnterObserver, PageConfigurator {

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabToTarget = new HashMap<>();

    private AppUtils appUtils;

    /**
     * Creates Main Application (NavBar, Menu and Content) View.
     *
     * @param appUtils application utils for determine dev mode
     */
    public MainView(final AppUtils appUtils) {
        this.appUtils = appUtils;

        String siteTitle = appUtils.getEnv().getProperty("eu.yals.site-title", "Yals").toUpperCase();

        DrawerToggle toggle = new DrawerToggle();
        setPrimarySection(Section.NAVBAR);

        Span title = new Span(siteTitle);
        title.addClassName("site-title");
        addToNavbar(toggle, title);

        //items
        addLogo();
        addSubTitle();
        addMenuTab("Main", HomeView.class, VaadinIcon.HOME);
        addMenuTab("App Info", AppInfoView.class, VaadinIcon.INFO);

        // dev-only items
        if (appUtils.isDevelopmentModeActivated()) {
            addMenuTab("Debug", DebugView.class, VaadinIcon.FLASK);
        }
        //link to old yals
        addLinkToOldYals();

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
    }

    private void addLogo() {
        Image logo = new Image("/images/logo.png", "Icon");
        logo.addClassName("logo-image");
        Tab logoTab = new Tab(logo);
        logoTab.setEnabled(false);
        tabs.add(logoTab);
    }

    private void addSubTitle() {
        Tab subTitleTab = new Tab("Yet another link shortener");
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

    private void addLinkToOldYals() {
        Icon icon = VaadinIcon.STEP_BACKWARD.create();
        Span label = new Span("Old UI");
        Anchor link = new Anchor(appUtils.getOldUILocation(), icon, label);
        Tab tab = new Tab(link);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(tabToTarget.get(beforeEnterEvent.getNavigationTarget()));
    }

    @Override
    public void configurePage(final InitialPageSettings settings) {
        settings.addFavIcon("icon", "/icons/favicon-32x32.png", "32x32");
        settings.addLink("shortcut icon", "/icons/favicon-16x16.png");
        settings.addLink("apple-touch-icon", "/icons/apple-touch-icon.png");
        settings.addLink("manifest", "/site.webmanifest");
        settings.addLink("mask-icon", "/icons/safari-pinned-tab.svg");

        settings.addMetaTag("apple-mobile-web-app-title", "Yals");
        settings.addMetaTag("application-name", "Yals");
        settings.addMetaTag("msapplication-TileColor", "#ffc40d");
        settings.addMetaTag("theme-color", "#ffffff");
    }
}
