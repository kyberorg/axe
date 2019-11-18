package eu.yals.test.ui.vaadin.elements;

import com.vaadin.flow.component.board.testbench.RowElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import eu.yals.ui.HomeView;

@Attribute(name = "id", value = HomeView.VIEW_ID)
public class HomeViewElement extends TestBenchElement {

    //main area
    public final RowElement MAIN_ROW = $(RowElement.class).id(HomeView.MAIN_ROW_ID);
    public final H2Element TITLE = $(H2Element.class).first();


    //TODO remove after PoC
    public void focusOnTitle() {
        TITLE.focus();
    }
}
