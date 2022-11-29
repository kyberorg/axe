package pm.axe.ui.pages.debug;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Debug Page - Axe.pm")
public class DebugPage extends AxeBaseLayout implements BeforeEnterObserver {

    private final Span axeSessionSpan = new Span();
    private final Span vaadinSessionSpan = new Span();

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        setId(DebugPage.class.getSimpleName());
        removeAll();
        add(new H2("Debug Page"));

        AxeSession.getCurrent().ifPresent(session -> {
            axeSessionSpan.setText("Axe Session ID: " + session.getSessionId());
            add(axeSessionSpan);
        });

        final boolean hasVaadinSession = VaadinSession.getCurrent() != null
                && VaadinSession.getCurrent().getSession() != null
                && VaadinSession.getCurrent().getSession().getId() != null;
        if (hasVaadinSession) {
            vaadinSessionSpan.setText("Vaadin Session ID: " + VaadinSession.getCurrent().getSession().getId());
            add(vaadinSessionSpan);
        }

        add(new Span("Ready to debug something..."));
    }
}
