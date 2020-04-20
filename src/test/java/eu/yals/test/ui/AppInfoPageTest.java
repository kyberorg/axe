package eu.yals.test.ui;

import eu.yals.Endpoint;
import eu.yals.test.ui.pageobjects.AppInfoPageObject;

/**
 * Base for all tests testing HomePage aka /
 *
 * @since 2.7
 */
public abstract class AppInfoPageTest extends VaadinTest {
    protected AppInfoPageObject appInfoView;

    public void openPage() {
        open("/" + Endpoint.UI.INFO_PAGE);
        appInfoView = AppInfoPageObject.getPageObject(getDriver());
    }
}