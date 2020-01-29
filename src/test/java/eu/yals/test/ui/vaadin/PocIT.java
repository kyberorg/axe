package eu.yals.test.ui.vaadin;

import eu.yals.test.ui.vaadin.commons.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import org.junit.Assert;
import org.junit.Test;

public class PocIT extends VaadinTest<HomeViewPageObject> {
    @Override
    protected HomeViewPageObject openView() {
        return $(HomeViewPageObject.class).waitForFirst();
    }

    @Test
    public void testVaadin() {
        HomeViewPageObject homeView = openView();

        String titleText = homeView.getTitleField().getText();
        Assert.assertEquals("Yet another link shortener", titleText);

    }
}
