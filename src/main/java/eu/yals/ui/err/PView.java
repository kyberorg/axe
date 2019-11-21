package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.constants.Header;

@SpringComponent
@UIScope
@Route("void")
public class PView extends VerticalLayout implements HasErrorParameter<ArithmeticException> {

    public PView() {
        add(new Text("302 - OK"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ArithmeticException> parameter) {
        VaadinResponse.getCurrent().setHeader(Header.LOCATION, parameter.getCustomMessage());
        return 302;
    }
}
