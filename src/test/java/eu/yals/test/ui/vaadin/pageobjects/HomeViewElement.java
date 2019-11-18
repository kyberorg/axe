package eu.yals.test.ui.vaadin.pageobjects;

import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewElement extends TestBenchElement {

    public final H2Element TITLE = getMainArea().title;

    private MainAreaElement getMainArea() {
        return new MainAreaElement();
    }

    public static class MainAreaElement extends TestBenchElement {
        private final VerticalLayoutElement self = $(VerticalLayoutElement.class).id(HomeView.MAIN_AREA_ID);
        private final H2Element title = self.$(H2Element.class).first();
    }

}
