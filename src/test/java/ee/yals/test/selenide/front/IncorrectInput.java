package ee.yals.test.selenide.front;

import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Rule;
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
    @Rule // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenShotsOnFail = ScreenShooter.failedTests();

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void emptyInput() {
        pasteValueInFormAndSubmitIt("");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("cannot be empty"));
    }

    @Test
    public void singleSpace() {
        pasteValueInFormAndSubmitIt(" ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("cannot be empty"));

    }

    @Test
    public void twoSpaces() {
        pasteValueInFormAndSubmitIt("  ");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("cannot be empty"));

    }

    @Test
    public void shortVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("ggg");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(and("short and notURL text", text("valid URL")));
    }

    @Test
    public void longVariantOfNotUrlInput() {
        pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("valid URL"));
    }

    @Test
    public void urlWithSpacesShallNotPass() {
        pasteValueInFormAndSubmitIt("http://site with spaces.com");

        formIsClearedAndResultNotVisible();
        errorBoxShouldAppear();
        $("#errorText").shouldHave(text("valid URL"));
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
