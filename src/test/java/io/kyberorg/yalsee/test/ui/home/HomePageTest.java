package io.kyberorg.yalsee.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.VR;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.SelenideUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.TestUtils.addRedirectPageBypassSymbol;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Testing /(Slash) URL.
 *
 * @since 1.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class HomePageTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
        open("/");
        waitForVaadin();
    }

    /**
     * On URL with slash (/) only opens front page.
     */
    @Test
    public void urlWithJustSlashWillOpenFrontPage() {
        HomePageObject.MainArea.LONG_URL_INPUT.should(exist);
        HomePageObject.MainArea.SUBMIT_BUTTON.should(exist);
    }

    /**
     * Verifies that correct original site opens when clicked to link in result.
     */
    @Test
    public void saveLinkAndClickOnResult() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;

        $(shortLink).shouldBe(visible);
        shortLink.click();

        verifyThatRedirectPageOpened();
    }

    /**
     * Saves link, copies result, opens it and verifies that correct original site opens.
     */
    @Test
    public void saveLinkAndCopyValueAndOpenIt() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;
        $(shortLink).shouldBe(visible);
        String shortUrl = shortLink.getText();

        open(shortUrl + addRedirectPageBypassSymbol());
        verifyThatVROpened();
    }

    /**
     * Saves link by clicking enter, copies result, opens it and verifies that correct original site opens.
     */
    @Test
    public void saveLinkByClickingEnterAndVerify() {
        HomePageObject.pasteValueInForm("https://vr.fi");
        HomePageObject.MainArea.SUBMIT_BUTTON.pressEnter();

        //dirty hack to handle double Enter (occurs time to time)
        closeErrorBoxIfDisplayed();

        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;
        $(shortLink).shouldBe(visible);
        String shortUrl = shortLink.getText();

        open(shortUrl + addRedirectPageBypassSymbol());
        verifyThatVROpened();
    }

    /**
     * Tests if request something that not exist yet, page 404 opens.
     */
    @Test
    public void openSomethingNonExisting() {
        open("/perkele");
        verifyThatPage404Opened();
    }

    /**
     * Tests if request something that not exist yet deeper than single level, page 404 opens.
     */
    @Test
    public void openSomethingNonExistingDeeperThanSingleLevel() {
        open("/void/something/here");
        verifyThatPage404Opened();
    }

    private void verifyThatVROpened() {
        Assertions.assertEquals(VR.TITLE_TEXT, SelenideUtils.getPageTitle());
    }

    private void verifyThatRedirectPageOpened() {
        RedirectPageObject.VIEW.should(exist);
    }

    private void verifyThatPage404Opened() {
        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }

    private void closeErrorBoxIfDisplayed() {
        if (HomePageObject.ErrorModal.ERROR_MODAL.isDisplayed()) {
            HomePageObject.ErrorModal.ERROR_BUTTON.click();
        }
    }
}
