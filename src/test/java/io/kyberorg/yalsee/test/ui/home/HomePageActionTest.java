package io.kyberorg.yalsee.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.HttpKyberorgIo;
import io.kyberorg.yalsee.test.pageobjects.external.QuayIo;
import io.kyberorg.yalsee.test.pageobjects.external.VR;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.utils.TestUtils.addRedirectPageBypassSymbol;

/**
 * Testing /(Slash) URL.
 *
 * @since 1.0
 */
public class HomePageActionTest extends SelenideTest {

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
        MainArea.LongURLInput.INPUT.should(exist);
        MainArea.SUBMIT_BUTTON.should(exist);
    }

    /**
     * Verifies that correct original site opens when clicked to link in result.
     */
    @Test
    public void saveLinkAndClickOnResult() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://vr.fi");
        SelenideElement shortLink = ResultArea.RESULT_LINK;

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
        SelenideElement shortLink = ResultArea.RESULT_LINK;
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
        MainArea.SUBMIT_BUTTON.pressEnter();

        //dirty hack to handle double Enter (occurs time to time)
        closeErrorBoxIfDisplayed();

        SelenideElement shortLink = ResultArea.RESULT_LINK;
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
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.DescriptionInput.INPUT.shouldBe(visible);
        MainArea.DescriptionInput.INPUT.shouldHave(attribute("placeholder"));
    }

    /**
     * Tests that description accordion opens and DescriptionInput becomes visible.
     */
    @Test
    public void accordionOpensAndDescriptionInputBecomesVisible() {
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.DESCRIPTION_ACCORDION.shouldHave(attribute("opened"));
        MainArea.DescriptionInput.INPUT.shouldBe(visible);
        MainArea.DescriptionInput.INPUT.shouldBe(enabled);
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
        ErrorModal.ERROR_MODAL.shouldBe(visible);
        closeErrorBoxIfDisplayed();
    }

    /**
     * Tests that when Link Input is empty and Description Input is filled - Error shown.
     */
    @Test
    public void whenLinkInputIsEmptyAndDescriptionInputFilledErrorShown() {
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.DescriptionInput.INPUT.setValue("Some description");

        MainArea.SUBMIT_BUTTON.click();

        ErrorModal.ERROR_MODAL.shouldBe(visible);
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
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.DescriptionInput.INPUT.setValue(description);

        MainArea.SUBMIT_BUTTON.click();

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
        MainArea.LongURLInput.INPUT.shouldBe(empty);
        MainArea.DescriptionInput.INPUT.shouldBe(empty);
    }

    /**
     * Test that when Link is empty and Description Input is filled Fields are cleaned up.
     */
    @Test
    public void whenLinkIsEmptyAndDescriptionInputIsFilledFieldsAreCleanedUp() {
        MainArea.DESCRIPTION_ACCORDION.click();
        HomePageObject.pasteValueInFormAndSubmitIt("");
        closeErrorBoxIfDisplayed();
        MainArea.LongURLInput.INPUT.shouldBe(empty);
        MainArea.DescriptionInput.INPUT.shouldBe(empty);
    }

    /**
     * Tests that link with spaces is trimmed and saved.
     */
    @Test
    public void linkWithSpacesShouldBeTrimmedAndSaved() {
        HomePageObject.pasteValueInFormAndSubmitIt(" https://quay.io/kyberorg/yalsee-base");

        SelenideElement shortLink = ResultArea.RESULT_LINK;
        $(shortLink).shouldBe(visible);
        String shortUrl = shortLink.getText();

        open(shortUrl + addRedirectPageBypassSymbol());
        verifyThatQuayIoOpened();
    }

    /**
     * Tests that there is no default option within protocol selector aka no option selected.
     */
    @Test
    public void protocolSelectorHasNoDefaultOption() {
        HomePageObject.pasteValueInFormAndSubmitIt("kv.ee");
        MainArea.ProtocolSelector.HTTPS_OPTION.shouldNotBe(selected);
        MainArea.ProtocolSelector.HTTP_OPTION.shouldNotBe(selected);
        MainArea.ProtocolSelector.FTP_OPTION.shouldNotBe(selected);
    }

    /**
     * Link not saved when no Protocol selected.
     */
    @Test
    public void noProtocolSelected_linkNotSaved() {
        HomePageObject.pasteValueInFormAndSubmitIt("kv.ee");
        ResultArea.RESULT_AREA.shouldNotBe(visible);
    }

    /**
     * No Protocol selected - Error Message shown.
     */
    @Test
    public void noProtocolSelected_ErrorMessageShown() {
        HomePageObject.pasteValueInFormAndSubmitIt("kv.ee");
        MainArea.ProtocolSelector.ERROR_MESSAGE.shouldBe(visible);
        MainArea.ProtocolSelector.ERROR_MESSAGE.shouldHave(text("Please select protocol"));
    }

    /**
     * When HTTPS protocol selector - link with HTTPS saved.
     */
    @Test
    public void whenHttpsProtocolSelected_linkWithHttpsSaved() {
        HomePageObject.pasteValueInForm("vr.fi");
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.ProtocolSelector.HTTPS_OPTION.click();
        MainArea.SUBMIT_BUTTON.click();
        String savedLink = HomePageObject.getSavedUrl();
        open(savedLink + addRedirectPageBypassSymbol());
        verifyThatVROpened();
    }

    /**
     * When HTTP protocol selector - link with HTTP saved.
     */
    @Test
    public void whenHttpProtocolSelected_linkWithHttpSaved() {
        HomePageObject.pasteValueInForm("http.kyberorg.io");
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.ProtocolSelector.HTTP_OPTION.click();
        MainArea.SUBMIT_BUTTON.click();
        String savedLink = HomePageObject.getSavedUrl();
        open(savedLink + addRedirectPageBypassSymbol());
        verifyThatHttpKyberorgIoOpened();
    }

    /**
     * When FTP protocol selector - link with FTP saved.
     */
    @Test
    public void whenFtpProtocolSelected_linkWithFtpSaved() {
        HomePageObject.pasteValueInForm("ftp.gwdg.de/pub/linux/manjaro/");
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.ProtocolSelector.FTP_OPTION.click();
        MainArea.SUBMIT_BUTTON.click();
        String savedLink = HomePageObject.getSavedUrl();
        open(savedLink);
        RedirectPageObject.Links.TARGET_LINK.shouldHave(text("ftp://"));
    }

    /**
     * When protocol added to input - protocol selector should close.
     */
    @Test
    public void protocolAdded_protocolSelectorClosed() {
        HomePageObject.pasteValueInForm("vr.fi");
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.ProtocolSelector.SELECTOR.shouldBe(visible);
        HomePageObject.cleanInput();
        HomePageObject.pasteValueInForm("https://vr.fi");
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.ProtocolSelector.SELECTOR.shouldNotBe(visible);
    }

    /**
     * Test that clear button appears when there is text in input field.
     */
    @Test
    public void whenTextInLongURLInput_clearButtonAppears() {
        HomePageObject.pasteValueInForm("kv.ee");
        MainArea.LongURLInput.CLEAR_BUTTON.shouldBe(visible);
    }

    /**
     * Clear button actually clears input.
     */
    @Test
    public void clearButtonClearsInput() {
        HomePageObject.pasteValueInForm("kv.ee");
        MainArea.LongURLInput.CLEAR_BUTTON.click();
        MainArea.LongURLInput.INPUT.shouldBe(empty);
    }

    /**
     * Test that clear button disappears when text cleared.
     */
    @Test
    public void whenTextCleared_clearButtonShouldDisappear() {
        HomePageObject.pasteValueInForm("kv.ee");
        MainArea.LongURLInput.CLEAR_BUTTON.shouldBe(visible);
        HomePageObject.cleanInput();
        MainArea.LongURLInput.CLEAR_BUTTON.shouldNotBe(visible);
    }

    /**
     * Test that clear button appears when there is text in input field.
     */
    @Test
    public void whenTextInDescriptionInput_clearButtonAppears() {
        HomePageObject.fillInDescription("Kinnisvaraportaal Nr 1");
        MainArea.DescriptionInput.CLEAR_BUTTON.shouldBe(visible);
    }

    /**
     * Description Clear button actually clears description input.
     */
    @Test
    public void descriptionClearButtonClearsDescription() {
        HomePageObject.fillInDescription("Kinnisvaraportaal Nr 1");
        MainArea.DescriptionInput.CLEAR_BUTTON.click();
        MainArea.DescriptionInput.INPUT.shouldBe(empty);
    }

    /**
     * Test that clear button disappears when text cleared.
     */
    @Test
    public void whenDescriptionTextCleared_clearButtonShouldDisappear() {
        HomePageObject.fillInDescription("Kinnisvaraportaal Nr 1");
        MainArea.DescriptionInput.CLEAR_BUTTON.shouldBe(visible);
        HomePageObject.cleanDescription();
        MainArea.DescriptionInput.CLEAR_BUTTON.shouldNotBe(visible);
    }

    private void verifyThatVROpened() {
        VR.VR_LOGO.shouldBe(visible);
    }

    private void verifyThatQuayIoOpened() {
        QuayIo.LOGO.should(exist);
        QuayIo.LOGO.shouldBe(visible);
    }

    private void verifyThatHttpKyberorgIoOpened() {
        HttpKyberorgIo.TITLE.shouldBe(visible);
        HttpKyberorgIo.TITLE.shouldHave(text(HttpKyberorgIo.TITLE_TEXT));
    }

    private void verifyThatRedirectPageOpened() {
        RedirectPageObject.VIEW.should(exist);
    }

    private void verifyThatPage404Opened() {
        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }

    private void closeErrorBoxIfDisplayed() {
        if (ErrorModal.ERROR_MODAL.isDisplayed()) {
            ErrorModal.ERROR_BUTTON.click();
        }
    }
}
