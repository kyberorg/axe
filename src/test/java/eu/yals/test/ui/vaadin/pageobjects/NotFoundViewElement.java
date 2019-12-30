package eu.yals.test.ui.vaadin.pageobjects;


import com.codeborne.selenide.SelenideElement;
import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.tech.ElementConverter;

public class NotFoundViewElement extends TestBenchElement {

    public final SelenideElement TITLE = ElementConverter.get().convert(getTitle());

    public H1Element getTitle() {
        return $(H1Element.class).first();
    }

    public SpanElement getSubtitle() {
        return $(SpanElement.class).first();
    }

    public ImageElement getImage() {
        return $(ImageElement.class).first();
    }
}
