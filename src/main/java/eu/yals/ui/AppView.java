package eu.yals.ui;

import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftHeaderItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.Section;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import eu.yals.ui.dev.AppInfoView;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
public class AppView extends AppLayoutRouterLayout<LeftLayouts.LeftHybrid>
        implements PageConfigurator {
    private static final String TAG = "[App View]";

    public AppView(AppUtils appUtils) {

        AppLayoutBuilder<LeftLayouts.LeftHybrid> builder =
                AppLayoutBuilder.get(LeftLayouts.LeftHybrid.class).withTitle("YALS");

        LeftAppMenuBuilder menuBuilder = LeftAppMenuBuilder.get();

        // title and subtitle
        menuBuilder.addToSection(
                Section.HEADER,
                new LeftHeaderItem(
                        "Yet another link shortener",
                        "",
                        "/images/logo.png"));

        // items
        menuBuilder.add(new LeftNavigationItem(HomeView.class));

        // dev-only items
        if (appUtils.isDevelopmentModeActivated()) {
            menuBuilder.add(new LeftNavigationItem(DebugView.class));
            menuBuilder.add(new LeftNavigationItem(AppInfoView.class));
        }

        builder.withAppMenu(menuBuilder.build());

        LeftLayouts.LeftHybrid layout = builder.build();
        init(layout);
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
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
