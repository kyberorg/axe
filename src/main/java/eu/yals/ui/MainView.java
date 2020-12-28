package eu.yals.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.ui.dev.AppInfoView;

import java.util.HashMap;
import java.util.Map;

@SpringComponent
@UIScope
@Route("tmp")
public class MainView extends AppLayout implements BeforeEnterObserver {

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> targets = new HashMap<>();

    public MainView() {
            Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
            img.setHeight("63px");
            addToNavbar(new DrawerToggle(), img);

            addMenuTab("Main", HomeView.class);
            addMenuTab("App", AppInfoView.class);

            tabs.setOrientation(Tabs.Orientation.VERTICAL);
            addToDrawer(tabs);
    }

    private void addMenuTab(String label, Class<? extends Component> target) {
        RouterLink link = new RouterLink(null, target);
        link.add(VaadinIcon.FLASK.create());
        link.add(label);
        Tab tab = new Tab(link);
        targets.put(target, tab);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(targets.get(beforeEnterEvent.getNavigationTarget()));
    }
}
