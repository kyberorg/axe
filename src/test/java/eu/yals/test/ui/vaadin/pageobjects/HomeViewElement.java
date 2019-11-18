package eu.yals.test.ui.vaadin.pageobjects;

import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;

@Attribute(name = "id", value = "home-view")
public class HomeViewElement extends TestBenchElement {

    public final H2Element TITLE = getMainArea().title;

    private MainAreaElement getMainArea() {
        return new MainAreaElement();
    }

    @Attribute(name = "id", value = "main-area")
    public static class MainAreaElement extends TestBenchElement {
        private final H2Element title = this.$(H2Element.class).first();
    }

}
