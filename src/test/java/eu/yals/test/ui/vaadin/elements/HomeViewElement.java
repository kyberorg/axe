package eu.yals.test.ui.vaadin.elements;

import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewElement extends TestBenchElement {

    public H2Element title() {
        return $(H2Element.class).first();
    }

    //TODO remove after PoC
    public void focusOnTitle() {
        title().focus();
    }
}
