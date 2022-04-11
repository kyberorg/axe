package io.kyberorg.yalsee.ui.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Section extends Composite<VerticalLayout> {
    private final H3 title = new H3();
    private final VerticalLayout elements = new VerticalLayout();

    public Section() {
        getContent().add(title, elements);
        getContent().addClassName("border");
        getContent().addClassName("white-border");
    }

    public void setSectionTitle(final String text) {
        title.setText(text);
    }

    public void addElements(Component... components) {
        elements.add(components);
    }
}
