package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.annotations.RunOnHub;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;
import org.junit.Assert;
import org.junit.Test;

@RunOnHub("ci.yadev.eu") //TODO customize
public class PocIT extends VaadinTest<HomeViewElement> {
    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).first();
    }

    @Test
    public void testVaadin() {
        HomeViewElement homeView = openView();

        String titleText = homeView.TITLE.getText();
        Assert.assertEquals("Yet another link shortener", titleText);
    }
}
