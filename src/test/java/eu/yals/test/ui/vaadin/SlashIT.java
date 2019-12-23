package eu.yals.test.ui.vaadin;

import eu.yals.test.ui.vaadin.commons.SlashCommons;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewElement;
import eu.yals.test.ui.vaadin.pageobjects.external.VR;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class SlashIT extends SlashCommons {
    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    @Test
    public void saveLinkAndClickOnResult() {
        HomeViewElement homeView = openView();
        pasteValueInFormAndSubmitIt("https://vr.fi");

        homeView.getShortLinkField().click();

        verifyThatVROpened();
    }

    private void verifyThatVROpened() {
        WebElement logo = findElement(VR.LOGO);
        String logoAttribute = logo.getAttribute("alt");
        Assert.assertEquals("VR", logoAttribute);
    }
}
