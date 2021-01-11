package eu.yals.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class DebugView extends Div {

    /**
     * Creates {@link DebugView}.
     */
    public DebugView() {
        setId(DebugView.class.getSimpleName());
    }
}
