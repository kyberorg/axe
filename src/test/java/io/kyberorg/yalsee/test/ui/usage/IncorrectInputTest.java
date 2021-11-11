package io.kyberorg.yalsee.test.ui.usage;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.utils.UrlExtraValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ErrorModal.*;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.MainArea.LONG_URL_INPUT;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ResultArea.RESULT_AREA;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Tries to input non-valid values and checks returned result.
 *
 * @since 1.0
 */
@Execution(ExecutionMode.CONCURRENT)
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
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * On empty input form should be cleaned, result and QR Code areas are not visible.
     */
    @Test
    public void emptyInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("");

        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
        formIsClearedResultAndQRCodeAreNotVisible();
    }

    /**
     * On input with single space only error box should appear.
     */
    @Test
    public void singleSpace() {
        HomePageObject.pasteValueInFormAndSubmitIt(" ");

        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    /**
     * On input with two spaces error box should appear.
     */
    @Test
    public void twoSpaces() {
        HomePageObject.pasteValueInFormAndSubmitIt("  ");

        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    /**
     * On input with non-valid stuff,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void shortVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("g&%g");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with non-valid stuff,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void longVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with URL that contains spaces,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithSpacesShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://site with spaces.com");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(UrlExtraValidator.URL_NOT_VALID));
    }

    /**
     * On input with URL that contains special chars,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithSpecialCharsShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://f%&k.com");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    /**
     * On input with URL contains bad protocol,
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlWithBadProtocolShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("file:///etc/passwd");
        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text("protocol not supported"));
    }

    /**
     * On input with URL that has single layer (without domain),
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlSingleLayerDomainLinksAreNotAllowed() {
        HomePageObject.pasteValueInFormAndSubmitIt("localhost");
        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(NOT_ALLOWED_TEXT));
    }

    /**
     * On input with URL that chas single layer (without domain),
     * form should be cleaned, result and QR Code areas are not visible and error box appears.
     */
    @Test
    public void urlSingleDomainLinksAreNotAllowed() {
        HomePageObject.pasteValueInFormAndSubmitIt("localhost/ff.ff");
        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(NOT_ALLOWED_TEXT));
    }

    private void errorBoxShouldAppear() {
        ERROR_MODAL.shouldBe(visible);
        ERROR_TEXT.shouldNotBe(empty);
        ERROR_BUTTON.shouldBe(visible);
    }

    private void formIsClearedResultAndQRCodeAreNotVisible() {
        RESULT_AREA.shouldNotBe(visible);
        QR_CODE_AREA.shouldNotBe(visible);
        LONG_URL_INPUT.shouldBe(empty);
    }

}
