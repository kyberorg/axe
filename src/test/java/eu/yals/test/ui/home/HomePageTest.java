package eu.yals.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.pageobjects.HomePageObject;
import eu.yals.test.pageobjects.NotFoundViewPageObject;
import eu.yals.test.pageobjects.external.VR;
import eu.yals.test.ui.SelenideTest;
import eu.yals.test.utils.SelenideUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.pageobjects.HomePageObject.MainArea.LONG_URL_INPUT;
import static eu.yals.test.pageobjects.HomePageObject.MainArea.SUBMIT_BUTTON;

/**
 * Testing /(Slash) URL.
 *
 * @since 1.0
 */
@SpringBootTest
public class HomePageTest extends SelenideTest {

    @Before
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
    }

    @Test
    public void urlWithJustSlashWillOpenFrontPage() {
        LONG_URL_INPUT.should(exist);
        SUBMIT_BUTTON.should(exist);
    }

    @Test
    public void saveLinkAndClickOnResult() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;

        $(shortLink).shouldBe(visible);
        shortLink.click();

        verifyThatVROpened();
    }

    @Test
    public void saveLinkAndCopyValueAndOpenIt() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;
        $(shortLink).shouldBe(visible);
        String shortUrl = shortLink.getText();

        open(shortUrl);
        verifyThatVROpened();
    }

    @Test
    public void openSomethingNonExisting() {
        open("/perkele");
        verifyThatPage404Opened();
    }

    @Test
    public void openSomethingNonExistingDeeperThanSingleLevel() {
        open("/void/something/here");
        verifyThatPage404Opened();
    }

    private void verifyThatVROpened() {
        Assert.assertEquals(VR.TITLE_TEXT, SelenideUtils.getPageTitle());
    }

    private void verifyThatPage404Opened() {
        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }
}
