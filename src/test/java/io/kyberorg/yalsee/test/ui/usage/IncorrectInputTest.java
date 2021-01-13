package io.kyberorg.yalsee.test.ui.usage;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.utils.UrlExtraValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ErrorModal.*;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.MainArea.LONG_URL_INPUT;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.MainArea.SUBMIT_BUTTON;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ResultArea.RESULT_AREA;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

@SpringBootTest
public class IncorrectInputTest extends SelenideTest {
    private static final String CANNOT_EMPTY_TEXT = "cannot be empty";
    private static final String MALFORMED_URL_TEXT = "malformed URL or not URL";
    private static final String NOT_ALLOWED_TEXT = "temporary not allowed";

    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        waitForVaadin();
    }

    @Test
    public void emptyInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("");

        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
        formIsClearedResultAndQRCodeAreNotVisible();
    }

    @Test
    public void singleSpace() {
        HomePageObject.pasteValueInForm(" ");
        submitButtonShouldBeDisabled();
    }

    @Test
    public void twoSpaces() {
        HomePageObject.pasteValueInForm("  ");
        submitButtonShouldBeDisabled();
    }

    @Test
    public void shortVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("g&%g");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void longVariantOfNotUrlInput() {
        HomePageObject.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpacesShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://site with spaces.com");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(UrlExtraValidator.URL_NOT_VALID));
    }

    @Test
    public void urlWithSpecialCharsShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://f%&k.com");

        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithBadProtocolShallNotPass() {
        HomePageObject.pasteValueInFormAndSubmitIt("file:///etc/passwd");
        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text("protocol not supported"));
    }

    @Test
    public void urlSingleLayerDomainLinksAreNotAllowed() {
        HomePageObject.pasteValueInFormAndSubmitIt("localhost");
        formIsClearedResultAndQRCodeAreNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(NOT_ALLOWED_TEXT));
    }

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

    private void submitButtonShouldBeDisabled() {
        SUBMIT_BUTTON.shouldHave(attribute("disabled"));
    }
}
