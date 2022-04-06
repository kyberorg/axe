package io.kyberorg.yalsee.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.QuayIo;
import io.kyberorg.yalsee.test.pageobjects.external.VR;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
public class HomePageTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
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

    /**
     * Tests that description input has placeholder, and it is not empty.
     */
    @Test
    public void descriptionInputHasNotEmptyPlaceholder() {
        //open it
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldBe(visible);
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldHave(attribute("placeholder"));
    }

    /**
     * Tests that description accordion opens and DescriptionInput becomes visible.
     */
    @Test
    public void accordionOpensAndDescriptionInputBecomesVisible() {
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.shouldHave(attribute("opened"));
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldBe(visible);
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldBe(enabled);
    }

    /**
     * Tests that when Link Input filled and Description Input empty - Link stored and Description is empty.
     */
    @Test
    public void whenLinkInputFilledAndDescriptionInputEmptyLinkStoredDescriptionEmpty() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        open("/myLinks");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldBe(empty);
        MyLinksViewPageObject.cleanSession();
    }

    /**
     * Tests that when Link and Description Inputs are empty - Error shown.
     */
    @Test
    public void whenLinkAndDescriptionInputsAreEmptyErrorShown() {
        HomePageObject.pasteValueInFormAndSubmitIt("");
        HomePageObject.ErrorModal.ERROR_MODAL.shouldBe(visible);
        closeErrorBoxIfDisplayed();
    }

    /**
     * Tests that when Link Input is empty and Description Input is filled - Error shown.
     */
    @Test
    public void whenLinkInputIsEmptyAndDescriptionInputFilledErrorShown() {
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.MainArea.DESCRIPTION_INPUT.setValue("Some description");

        HomePageObject.MainArea.SUBMIT_BUTTON.click();

        HomePageObject.ErrorModal.ERROR_MODAL.shouldBe(visible);
        closeErrorBoxIfDisplayed();
    }

    /**
     * Tests that when Link and Description Inputs are filled - Both Link and Description are saved.
     */
    @Test
    public void whenLinkAndDescriptionInputsAreFilledBothSaved() {
        //cleaning session first
        open("/myLinks");
        MyLinksViewPageObject.cleanSession();
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
        open("/");
        waitForVaadin();

        String link = "https://vr.fi";
        String description = "Suomen junat";

        HomePageObject.pasteValueInForm(link);
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.MainArea.DESCRIPTION_INPUT.setValue(description);

        HomePageObject.MainArea.SUBMIT_BUTTON.click();

        open("/myLinks");
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldNotBe(empty);
        String actualDescription = descriptionCell.getText();

        //cleaning session afterwards
        MyLinksViewPageObject.cleanSession();
        Assertions.assertEquals(description, actualDescription);
    }

    /**
     * Test that when Link and Description Inputs are empty Fields are cleaned up.
     */
    @Test
    public void whenLinkAndDescriptionInputsAreEmptyFieldsAreCleanedUp() {
        HomePageObject.pasteValueInFormAndSubmitIt("");
        closeErrorBoxIfDisplayed();
        HomePageObject.MainArea.LONG_URL_INPUT.shouldBe(empty);
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldBe(empty);
    }

    /**
     * Test that when Link is empty and Description Input is filled Fields are cleaned up.
     */
    @Test
    public void whenLinkIsEmptyAndDescriptionInputIsFilledFieldsAreCleanedUp() {
        HomePageObject.MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.pasteValueInFormAndSubmitIt("");
        closeErrorBoxIfDisplayed();
        HomePageObject.MainArea.LONG_URL_INPUT.shouldBe(empty);
        HomePageObject.MainArea.DESCRIPTION_INPUT.shouldBe(empty);
    }

    /**
     * Tests that link with spaces is trimmed and saved.
     */
    @Test
    public void linkWithSpacesShouldBeTrimmedAndSaved() {
        HomePageObject.pasteValueInFormAndSubmitIt(" quay.io/kyberorg/yalsee-base");

        SelenideElement shortLink = HomePageObject.ResultArea.RESULT_LINK;
        $(shortLink).shouldBe(visible);
        String shortUrl = shortLink.getText();

        open(shortUrl + addRedirectPageBypassSymbol());
        verifyThatQuayIoOpened();
    }

    private void verifyThatVROpened() {
        VR.VR_LOGO.shouldBe(visible);
    }

    private void verifyThatQuayIoOpened() {
        QuayIo.LOGO.should(exist);
        QuayIo.LOGO.shouldBe(visible);
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
