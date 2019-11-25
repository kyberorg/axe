package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import eu.yals.Endpoint;
import eu.yals.ui.AppView;

@Route(value = Endpoint.VAADIN_APPLICATION_ERROR_PAGE, layout = AppView.class)
public class AppDownView extends VerticalLayout {

    public AppDownView() {
        add(new Text("503 - App Down"));
    }

}
