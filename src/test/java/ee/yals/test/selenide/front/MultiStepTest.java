package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void copyLinkButtonShouldCopyShortLink() {
        String longLink = "https://github.com/yadevee/yals";
        pasteValueInFormAndSubmitIt(longLink);

        $("div#result").shouldBe(visible);
        $("#copyLink").shouldBe(visible);

        $("#copyLink").click();
        $("#longUrl").click();
        $("#longUrl").sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String pastedText = $("#longUrl").text();
        assertEquals(longLink, pastedText);
    }

    private void pasteValueInFormAndSubmitIt(String link) {
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }
}
