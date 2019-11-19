package eu.yals.test.ui.vaadin.pageobjects;

import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewElement extends TestBenchElement {

    public H2Element getTitle() {
        return getMainArea().$(H2Element.class).first();
    }

    public TestBenchElement getMainArea() {
        return $(TestBenchElement.class).id(HomeView.MAIN_AREA_ID);
    }


}
