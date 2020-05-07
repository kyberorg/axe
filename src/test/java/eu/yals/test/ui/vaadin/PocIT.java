package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.annotations.RunOnHub;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPage;
import org.junit.Assert;
import org.junit.Test;

@RunOnHub("ci.yadev.eu") //TODO customize
public class PocIT extends VaadinTest<HomeViewPage> {

    @Override
    protected HomeViewPage openView() {
        return $(HomeViewPage.class).waitForFirst();
    }

    @Test
    public void testVaadin() {
        HomeViewPage homeView = $(HomeViewPage.class).first();

        String title = homeView.title.getText();
        Assert.assertEquals("Yet another link shortener", title);
    }
}
