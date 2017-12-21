package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import ee.yals.test.utils.Selenide;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

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

    @Test
    public void copyLinkButtonShouldCopyShortLink() {
        if (isBrowserHtmlUnit()) {
            Assume.assumeTrue("Paste (multi-key aka Ctrl+V) " +
                            "not working in " + Selenide.Browser.HTMLUNIT + ". Test ignored",
                    true);
            return;
        }
        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        $("div#result").shouldBe(visible);
        $("#copyLink").shouldBe(visible);

        $("#copyLink").click();
        $("#longUrl").click();
        $("#longUrl").sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String shortLink = $("#resultLink").text();

        String pastedLink = $("#longUrl").val();
        Assert.assertEquals(shortLink, pastedLink);
    }

    @Test
    public void linksCounterIncreasedValueAfterSave() {
        long initialNumber = Long.parseLong($("#overallLinksNum").text());
        Assert.assertEquals(0, initialNumber);

        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        long numberAfterLinkSaved = Long.parseLong($("#overallLinksNum").text());
        Assert.assertEquals(initialNumber + 1, numberAfterLinkSaved);
    }

    private void pasteValueInFormAndSubmitIt(String link) {
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }
}
