package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;

@SpringComponent
@UIScope
@Route(Endpoint.VAADIN_ERROR_PAGE)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<Exception> {

    public ServerErrorView() {
        add(new Text("500 - Server Error"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        return 500;
    }
}
