package eu.yals.test.ui.vaadin.pageobjects;

import com.vaadin.flow.component.board.testbench.RowElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewPage extends TestBenchElement {

    public final H2Element title = getMainArea().title;

    private MainRowElement getMainArea() {
        return new MainRowElement();
    }

    public static class MainRowElement extends TestBenchElement {
        private final RowElement self = $(RowElement.class).id(HomeView.MAIN_ROW_ID);
        private final H2Element title = self.$(H2Element.class).first();
    }


}
