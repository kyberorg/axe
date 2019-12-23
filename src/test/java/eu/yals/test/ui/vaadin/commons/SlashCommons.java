package eu.yals.test.ui.vaadin.commons;

import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;

public class SlashCommons extends VaadinTest<HomeViewElement> {
    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    protected void pasteValueInFormAndSubmitIt(String link) {
        HomeViewElement homeView = openView();
        homeView.getInputField().setValue(link);
        homeView.getSubmitButton().click();
    }
}
