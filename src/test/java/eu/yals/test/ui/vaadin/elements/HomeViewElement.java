package eu.yals.test.ui.vaadin.elements;

import com.vaadin.flow.component.board.testbench.RowElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewElement extends TestBenchElement {

    public static class MainRowElement extends TestBenchElement {
        public final RowElement SELF = $(RowElement.class).id(HomeView.MAIN_ROW_ID);
        public final H2Element TITLE = SELF.$(H2Element.class).first();
    }

    //main area
    public MainRowElement getMainRow() {
        return new MainRowElement();
    }


    //TODO remove after PoC
    public void focusOnTitle() {
        getMainRow().TITLE.focus();
    }
}
