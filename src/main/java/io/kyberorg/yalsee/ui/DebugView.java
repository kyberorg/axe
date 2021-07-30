package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class DebugView extends YalseeLayout implements SessionInitListener {

    /**
     * Creates {@link DebugView}.
     */
    public DebugView() {
        setId(DebugView.class.getSimpleName());
        add(new H2("Debug Page"));
        add(new Text("Ready to debug something..."));
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        add(new Text("Session ID:" + event.getSession().getSession().getId()));
        add(new Text("Created: " + event.getSession().getSession().getCreationTime()));
    }
}
