package eu.yals.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringComponent
@UIScope
@Route("vaadin")
public class VaadinView extends VerticalLayout {
    public VaadinView() {
        add(new H1("Vaadin Works!"));
        log.info("[Vaadin View] Vaadin requested");
    }
}
