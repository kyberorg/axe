package pm.axe.test.ui.usage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.utils.UrlExtraValidator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tries to input non-valid values and checks returned result.
 *
 * @since 1.0
 */
public class IncorrectInputTest extends SelenideTest {
    private static final String CANNOT_EMPTY_TEXT = "cannot be empty";
    private static final String MALFORMED_URL_TEXT = "malformed URL or not URL";
    private static final String NOT_ALLOWED_TEXT = "temporary not allowed";

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * On empty input form should be cleaned, result and QR Code areas are not visible.
     */
    @Test
    public void emptyInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("");

        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
    }

    /**
     * On input with single space only error box should appear.
     */
    @Test
    public void singleSpace() {
        HomePageObject.pasteValueInFormAndSubmitIt(" ");

        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    /**
     * On input with two spaces error box should appear.
     */
    @Test
    public void twoSpaces() {
        HomePageObject.pasteValueInFormAndSubmitIt("  ");

        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    /**
     * On input with non-valid stuff,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void shortVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("g&%g");

        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with non-valid stuff,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void longVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with URL that contains spaces,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithSpacesShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://site with spaces.com");

        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(UrlExtraValidator.URL_NOT_VALID));
    }

    /**
     * On input with URL that contains special chars,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithSpecialCharsShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://f%&k.com");

        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with URL contains bad protocol,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithBadProtocolShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("file:///etc/passwd");
        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text("protocol not supported"));
    }

    /**
     * On input with URL that has single layer (without domain),
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlSingleLayerDomainLinksAreNotAllowed() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://machine");
        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(NOT_ALLOWED_TEXT));
    }

    /**
     * On input with URL that chas single layer (without domain),
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlSingleDomainLinksAreNotAllowed() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://machine/ff.ff");
        linkIsNotSavedAndResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        HomePageObject.ErrorModal.ERROR_TEXT.shouldHave(text(NOT_ALLOWED_TEXT));
    }

    private void errorBoxShouldAppear() {
        HomePageObject.ErrorModal.ERROR_MODAL.shouldBe(visible);
        HomePageObject.ErrorModal.ERROR_TEXT.shouldNotBe(empty);
        HomePageObject.ErrorModal.ERROR_BUTTON.shouldBe(visible);
    }

    private void linkIsNotSavedAndResultAndQRCodeAreNotVisible() {
        HomePageObject.ResultArea.RESULT_AREA.shouldNotBe(visible);
        HomePageObject.QrCodeArea.QR_CODE_AREA.shouldNotBe(visible);
    }

}
