package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("503")
public class AppDownView extends VerticalLayout {

    public AppDownView() {
        add(new Text("503 - App Down"));
    }

}
