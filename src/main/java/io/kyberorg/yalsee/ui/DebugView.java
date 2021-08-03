package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class DebugView extends YalseeLayout {

    /**
     * Creates {@link DebugView}.
     */
    public DebugView() {
        setId(DebugView.class.getSimpleName());
        add(new H2("Debug Page"));
        add(new Text("Ready to debug something..."));
        add(new Text("User-Agent is: " + VaadinRequest.getCurrent().getHeader("User-Agent")));
    }
}
