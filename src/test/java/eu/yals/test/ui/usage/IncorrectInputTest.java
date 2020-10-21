package eu.yals.test.ui.usage;

import com.codeborne.selenide.Condition;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import eu.yals.test.ui.HomePageTest;
import eu.yals.test.ui.SelenideTest;
import eu.yals.test.ui.pageobjects.uus.HomePageObject;
import eu.yals.utils.UrlExtraValidator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static com.helger.commons.mock.CommonsAssert.fail;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ErrorModal.*;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.MainArea.LONG_URL_INPUT;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.MainArea.SUBMIT_BUTTON;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ResultArea.RESULT_AREA;

@SpringBootTest
public class IncorrectInputTest extends SelenideTest {
    private static final String CANNOT_EMPTY_TEXT = "cannot be empty";
    private static final String MALFORMED_URL_TEXT = "malformed URL or not URL";
    private static final String NOT_ALLOWED_TEXT = "temporary not allowed";

    @Before
    public void beforeTest() {
        open("/");
        updateTestNameHook();
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
        formIsClearedResultAndQRCodeAreNotVisible();
    }

    @Test
    public void twoSpaces() {
        HomePageObject.pasteValueInForm("  ");

        submitButtonShouldBeDisabled();
        formIsClearedResultAndQRCodeAreNotVisible();
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
        LONG_URL_INPUT.shouldBe(empty);
        RESULT_AREA.shouldNotBe(visible);
        QR_CODE_AREA.shouldNotBe(visible);
    }

    private void submitButtonShouldBeDisabled() {
        SUBMIT_BUTTON.shouldBe(disabled);
    }
}
