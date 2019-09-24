package eu.yals.test.ui.pages.front;

import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.FrontPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tries to fill some wrong values to longURL and tests reaction
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IncorrectInputTest extends UITest {
    private static final String CANNOT_EMPTY_TEXT = "cannot be empty";
    private static final String MALFORMED_URL_TEXT = "";

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void emptyInput() {
        pasteValueInFormAndSubmitIt("");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    @Test
    public void singleSpace() {
        pasteValueInFormAndSubmitIt(" ");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void twoSpaces() {
        pasteValueInFormAndSubmitIt("  ");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void shortVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("g&%g");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(and("short and notURL text", text(MALFORMED_URL_TEXT)));
    }

    @Test
    public void longVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpacesShallNotPass() {
        pasteValueInFormAndSubmitIt("http://site with spaces.com");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpecialCharsShallNotPass() {
        pasteValueInFormAndSubmitIt("http://f%&k.com");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithBadProtocolShallNotPass() {
        pasteValueInFormAndSubmitIt("file:///etc/passwd");

        formIsClearedResultAndQrCodeAreNotVisible();
        errorBoxShouldAppear();
        FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text("protocol not supported"));
    }

    private void errorBoxShouldAppear() {
        FrontPage.ErrorRow.ERROR_MODAL.shouldBe(visible);
        FrontPage.ErrorRow.ERROR_TEXT.shouldNotBe(empty);
        FrontPage.ErrorRow.ERROR_CLOSE.shouldBe(visible);
    }

    private void formIsClearedResultAndQrCodeAreNotVisible() {
        FrontPage.MainRow.LONG_URL_INPUT.shouldBe(empty);
        FrontPage.ResultRow.RESULT_DIV.shouldNotBe(visible);
        FrontPage.QrCodeRow.QR_CODE_DIV.shouldNotBe(visible);
    }
}
