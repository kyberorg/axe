package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tries to fill some wrong values to longURL and tests reaction
 *
 * @since 1.0
 */
public class IncorrectInput extends UITest {
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
        $("#errorText").shouldHave(text(CANNOT_EMPTY_TEXT));
    }

    @Test
    public void singleSpace() {
        pasteValueInFormAndSubmitIt(" ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void twoSpaces() {
        pasteValueInFormAndSubmitIt("  ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text(CANNOT_EMPTY_TEXT));

    }

    @Test
    public void shortVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("ggg");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(and("short and notURL text", text(MALFORMED_URL_TEXT)));
    }

    @Test
    public void longVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpacesShallNotPass() {
        pasteValueInFormAndSubmitIt("http://site with spaces.com");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithSpecialCharsShallNotPass(){
        pasteValueInFormAndSubmitIt("http://f%&k.com");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text(MALFORMED_URL_TEXT));
    }

    @Test
    public void urlWithBadProtocolShallNotPass() {
        pasteValueInFormAndSubmitIt("file:///etc/passwd");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("protocol not supported"));
    }

    private void pasteValueInFormAndSubmitIt(String link) {
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }

    private void errorBoxShouldAppear() {
        $("#error").shouldBe(visible);
        $("#errorText").shouldNotBe(empty);
        $("#errorClose").shouldBe(visible);
    }

    private void formIsClearedAndResultNotVisible() {
        $("#longUrl").shouldBe(empty);
        $("#result").shouldNotBe(visible);
    }
}
