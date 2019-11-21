package eu.yals.ui.err;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringComponent
@UIScope
@Route("404")
public class NotFoundView extends VerticalLayout {
    public NotFoundView() {
        add(new Span("404 - Nothing here....go away"));
    }
}
