package eu.yals.test.ui.vaadin;

import eu.yals.test.ui.vaadin.commons.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;
import eu.yals.test.ui.vaadin.pageobjects.NotFoundViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.external.VR;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.open;


public class SlashIT extends VaadinTest<HomeViewElement> {

    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    @Test
    public void saveLinkAndClickOnResult() {
        HomeViewElement homeView = openView();
        homeView.pasteValueInFormAndSubmitIt("https://vr.fi");

        homeView.getShortLinkField().click();

        verifyThatVROpened();
    }

    @Test
    public void saveLinkAndCopyValueAndOpenIt() {
        HomeViewElement homeView = openView();
        homeView.pasteValueInFormAndSubmitIt("https://vr.fi");

        String shortUrl = homeView.getShortLinkField().getText();
        Assert.assertTrue(StringUtils.isNotBlank(shortUrl));

        open(shortUrl);
        verifyThatVROpened();
    }

    @Test
    public void openSomethingNonExisting() {
        HomeViewElement homeView = openView();
        open("/perkele");
        verifyThatPage404Opened();
    }

    @Test
    public void openSomethingNonExistingDeeperThanSingleLevel() {
        open("/void/something/here");
        verifyThatPage404Opened();
    }

    private void verifyThatVROpened() {
        WebElement logo = findElement(VR.LOGO);
        String logoAttribute = logo.getAttribute("alt");
        Assert.assertEquals("VR", logoAttribute);
    }

    private void verifyThatPage404Opened() {
        NotFoundViewPageObject page404 = NotFoundViewPageObject.getPageObject(getDriver());
        Assert.assertTrue(page404.getTitle().isDisplayed());
        //selenideElement(page404.getTitle()).should(exist);
        Assert.assertTrue(page404.getTitleText().contains("404"));
    }
}
