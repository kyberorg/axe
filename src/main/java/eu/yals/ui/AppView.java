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
import com.vaadin.flow.server.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringComponent
@UIScope
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(name = "Yet another link shortener", shortName = "yals",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png"},
        description = "Yet another link shortener for friends")

@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class AppView extends AppLayoutRouterLayout<LeftLayouts.LeftHybrid> implements PageConfigurator {
    public AppView() {
        AppLayoutBuilder<LeftLayouts.LeftHybrid> builder = AppLayoutBuilder
                .get(LeftLayouts.LeftHybrid.class)
                .withTitle("YALS");

        LeftAppMenuBuilder menuBuilder = LeftAppMenuBuilder.get();

        //title
        WebBrowser browser = VaadinSession.getCurrent().getBrowser();
        if (browser.isAndroid() || browser.isIOS()) {
            menuBuilder.addToSection(Section.HEADER,
                    new LeftHeaderItem("Yet another link shortener", "Version 2.7", "/images/logo.png"));
        }

        //items
        menuBuilder.add(new LeftNavigationItem(HomeView.class));
        menuBuilder.add(new LeftNavigationItem(SampleView.class));

        builder.withAppMenu(menuBuilder.build());

        LeftLayouts.LeftHybrid layout = builder.build();
        init(layout);
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addFavIcon("icon", "/images/logo.png", "512x512");
    }
}
