package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Contains multi step tests for Front page
 *
 * @since 1.0
 */
public class MultiStepTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void closeButtonReallyClosesErrorDiv() {
        pasteValueInFormAndSubmitIt(" ");

        $("#errorClose").click();
        $("div#error").shouldNotBe(visible);

    }

    @Test
    public void closeButtonClosesErrorDivButNotRemoves() {
        pasteValueInFormAndSubmitIt(" ");

        $("#errorClose").click();
        $("div#error").shouldNotBe(visible);
        $("div#error").shouldBe(exist);
    }

    @Test
    public void shortenItButtonClearsResultAndValueIfVisible() {
        pasteValueInFormAndSubmitIt("http://virtadev.net");
        $("div#result").shouldBe(visible);
        $("input#longUrl").shouldBe(empty);

        pasteValueInFormAndSubmitIt("ggg");
        $("div#result").shouldNotBe(visible);
        $("#resultLink").shouldBe(empty);

    }

    private void pasteValueInFormAndSubmitIt(String link) {
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }
}
