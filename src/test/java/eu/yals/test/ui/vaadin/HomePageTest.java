package eu.yals.test.ui.vaadin;

import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;

public abstract class HomePageTest extends VaadinTest {
    protected HomeViewPageObject homeView;

    public void openHomePage() {
        open("/");
        homeView = HomeViewPageObject.getPageObject(getDriver());
    }
}
