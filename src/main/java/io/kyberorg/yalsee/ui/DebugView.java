package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class DebugView extends YalseeLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        setId(DebugView.class.getSimpleName());
        removeAll();
        add(new H2("Debug Page"));
        add(new Text("Ready to debug something..."));
    }
}
