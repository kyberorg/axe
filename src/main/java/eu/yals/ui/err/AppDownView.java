package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import eu.yals.Endpoint;
import eu.yals.ui.AppView;
import org.springframework.transaction.CannotCreateTransactionException;

@Route(value = Endpoint.UI.ERROR_PAGE_503, layout = AppView.class)
public class AppDownView extends VerticalLayout implements HasErrorParameter<CannotCreateTransactionException> {

    public AppDownView() {
        add(new Text("503 - App Down"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<CannotCreateTransactionException> parameter) {
        return 503;
    }
}
