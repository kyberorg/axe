package pm.axe.ui.pages.user;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.db.models.User;
import pm.axe.services.user.UserService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

import java.util.Optional;

/**
 * Page, which shown when confirmation performed by {@link ConfirmationView} succeeded.
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.WELCOME_PAGE, layout = MainView.class)
@PageTitle("Welcome - Axe.pm")
public class WelcomePage extends AxeBaseLayout implements BeforeEnterObserver {
    private final UserService userService;
    private final Span welcomeSpan = new Span("Welcome");
    /**
     * Creates {@link WelcomePage}.
     */
    public WelcomePage(UserService userService) {
        this.userService = userService;
        add(welcomeSpan);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        AxeSession.getCurrent().ifPresent(as -> {
            if (as.hasUser()) {
                Optional<User> boundUser = userService.getUserById(as.getUserId());
                boundUser.ifPresent(user -> welcomeSpan.setText("Welcome, " + user.getUsername()));
            }
        });
    }
}
