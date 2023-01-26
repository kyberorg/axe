package pm.axe.ui.pages.user.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.ui.pages.user.LoginPage;
import pm.axe.ui.pages.user.profile.tabs.DangerZoneTab;
import pm.axe.ui.pages.user.profile.tabs.ProfileTab;
import pm.axe.ui.pages.user.profile.tabs.SecurityTab;
import pm.axe.ui.pages.user.profile.tabs.SettingsTab;
import pm.axe.utils.AxeSessionUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/profile_page.css")
@Route(value = Endpoint.UI.PROFILE_PAGE, layout = MainView.class)
@PageTitle("My Profile - Axe.pm")
public class ProfilePage extends AxeCompactLayout implements BeforeEnterObserver {
    private final AxeSessionUtils axeSessionUtils;
    private final Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
    private final Tab securityTab = new Tab(VaadinIcon.SHIELD.create(), new Span("Security"));
    private final Tab settingsTab = new Tab(VaadinIcon.COG.create(), new Span("Settings"));
    private final Tab dangerZoneTab = new Tab(VaadinIcon.FIRE.create(), new Span("Danger Zone"));
    private final ProfileTab profileTabContent;
    private final SecurityTab securityTabContent;
    private final SettingsTab settingsTabContent;
    private final DangerZoneTab dangerZoneTabContent;
    private boolean pageAlreadyInitialized = false;
    private User user;

    private final Div content = new Div();
    private final Map<Tab, Component> contentMap = new LinkedHashMap<>();

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        user = axeSessionUtils.boundUserIfAny();
        if (Objects.isNull(user)) {
            event.forwardTo(LoginPage.class);
            return;
        }

        if (!pageAlreadyInitialized) {
            initPage();
            pageAlreadyInitialized = true;
        }
    }

    private void initPage() {
        removeAll();
        //content map
        contentMap.clear();
        contentMap.put(profileTab, profileTabContent);
        contentMap.put(securityTab, securityTabContent);
        contentMap.put(settingsTab, settingsTabContent);
        contentMap.put(dangerZoneTab, dangerZoneTabContent);

        //title
        H2 title = new H2("My Profile");
        title.setClassName("profile-title");

        //tabs
        //set icon on top on text
        contentMap.forEach((tab, content) -> tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP));
        //init content
        contentMap.forEach((tab, content) -> {
            if (content instanceof HasTabInit) {
                ((HasTabInit) content).tabInit(user);
            }
        });
        Tabs tabs = new Tabs();
        tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
        contentMap.forEach((tab, content) -> tabs.add(tab));
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.setWidthFull();

        add(title, tabs, content);
    }

    private void setContent(final Tab tab) {
        content.removeAll();
        if (tab == null) {
            return;
        }
        Component tabContent = contentMap.get(tab);
        content.add(tabContent);
    }
}
