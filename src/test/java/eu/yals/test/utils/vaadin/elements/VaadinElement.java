package eu.yals.test.utils.vaadin.elements;

import lombok.Getter;
import lombok.Setter;

public abstract class VaadinElement {
    @Setter
    protected String tag;
    @Getter
    @Setter
    protected String cssSelector;

    protected VaadinElement() {
        cssSelector = getTag();
    }

    public abstract String getTag();


}
