package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.pages.FrontSelectors.ErrorRow.*;
import static ee.yals.test.utils.pages.FrontSelectors.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.pages.FrontSelectors.ResultRow.RESULT_DIV;

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

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    @Test
    public void singleSpace() {
        pasteValueInFormAndSubmitIt(" ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void twoSpaces() {
        pasteValueInFormAndSubmitIt("  ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void shortVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("ggg");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(and("short and notURL text", text(MALFORMED_URL_TEXT)));
    }

    @Test
    public void longVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpacesShallNotPass() {
        pasteValueInFormAndSubmitIt("http://site with spaces.com");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpecialCharsShallNotPass(){
        pasteValueInFormAndSubmitIt("http://f%&k.com");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithBadProtocolShallNotPass() {
        pasteValueInFormAndSubmitIt("file:///etc/passwd");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        ERROR_TEXT.shouldHave(text("protocol not supported"));
    }

    private void errorBoxShouldAppear() {
        ERROR_MODAL.shouldBe(visible);
        ERROR_TEXT.shouldNotBe(empty);
        ERROR_CLOSE.shouldBe(visible);
    }

    private void formIsClearedAndResultNotVisible() {
        LONG_URL_INPUT.shouldBe(empty);
        RESULT_DIV.shouldNotBe(visible);
    }
}
