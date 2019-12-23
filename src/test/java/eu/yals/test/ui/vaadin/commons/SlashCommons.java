package eu.yals.test.ui.vaadin.commons;

import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;
import org.junit.Assert;
import org.junit.Test;

public class SlashCommons extends VaadinTest<HomeViewElement> {
    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    /**
     * Otherwise error (No runnable methods) given
     */
    @Test
    public void dummyTest() {
        Assert.assertTrue(true);
    }

    protected void pasteValueInFormAndSubmitIt(String link) {
        HomeViewElement homeView = openView();
        homeView.getInputField().setValue(link);
        homeView.getSubmitButton().click();
    }

}
