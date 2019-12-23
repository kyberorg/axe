package eu.yals.test.ui.vaadin.pageobjects;


import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.tech.PocObject;

public class NotFoundViewElement extends TestBenchElement {

    public PocObject getTitle() {
        return PocObject.fromTestBenchElement($(H1Element.class).first());
    }

    public SpanElement getSubtitle() {
        return $(SpanElement.class).first();
    }

    public ImageElement getImage() {
        return $(ImageElement.class).first();
    }
}
