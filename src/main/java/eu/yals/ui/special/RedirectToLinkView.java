package eu.yals.ui.special;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.Header;

@SpringComponent
@UIScope
@Route(Endpoint.REDIRECTOR)
public class RedirectToLinkView extends VerticalLayout implements HasErrorParameter<ArithmeticException> {

    public RedirectToLinkView() {
        add(new Text("Not intended for direct use. Needs parameter"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ArithmeticException> parameter) {
        VaadinResponse.getCurrent().setHeader(Header.LOCATION, parameter.getCustomMessage());
        return 302;
    }
}
