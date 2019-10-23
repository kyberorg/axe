package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringComponent
@UIScope
@Route("404-ident")
public class IdentNotFoundView extends VerticalLayout {

    public IdentNotFoundView() {
        add(new Text("404 - Ident not found"));
    }

}
