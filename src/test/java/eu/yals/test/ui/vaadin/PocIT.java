package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.parallel.TestNameSuffix;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Cookie;

public class PocIT extends VaadinTest<HomeViewElement> {
    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    @TestNameSuffix(property = "MySuperTest")
    @Test
    public void testVaadin() {
        HomeViewElement homeView = openView();

        Cookie cookie = new Cookie("zaleniumMessage", "Checking Title");
        getDriver().manage().addCookie(cookie);

        String titleText = homeView.getTitleField().getText();
        Assert.assertEquals("Yet another link shortener", titleText);

        //TODO move to TestWatcher
        Cookie cookie2 = new Cookie("zaleniumTestPassed", "true");
        getDriver().manage().addCookie(cookie2);

        Cookie cookie3 = new Cookie("zaleniumTestName", "true");
        getDriver().manage().addCookie(cookie3);
    }
}
