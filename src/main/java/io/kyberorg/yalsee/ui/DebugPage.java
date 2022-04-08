package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class DebugPage extends YalseeLayout implements BeforeEnterObserver {

    private final Span yalseeSessionSpan = new Span();
    private final Span vaadinSessionSpan = new Span();

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        setId(DebugPage.class.getSimpleName());
        removeAll();
        add(new H2("Debug Page"));

        YalseeSession.getCurrent().ifPresent(session -> {
            yalseeSessionSpan.setText("Yalsee Session ID: " + session.getSessionId());
            add(yalseeSessionSpan);
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
