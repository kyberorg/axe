package ee.yals.test.selenide.slash;

import com.codeborne.selenide.SelenideElement;
import ee.yals.test.selenide.UITest;
import ee.yals.test.utils.Selenide;
import ee.yals.test.utils.TestUtils;
import org.junit.Assume;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing /(Slash) URL
 *
 * @since 1.0
 */
public class SlashUI extends UITest {

    @Test
    public void urlWithJustSlashWillOpenFrontPage() {
        open("/");
        $("#longUrl").shouldBe(exist);
        $("#shortenIt").shouldBe(exist);
    }

    @Test
    public void saveLinkAndClickOnResult() {
        boolean browserIsHtmlUnit = TestUtils.whichBrowser().equals(Selenide.Browser.HTMLUNIT);

        if (browserIsHtmlUnit) {
            Assume.assumeTrue("External resources not working with " + Selenide.Browser.HTMLUNIT + ". Test ignored",
                    true);
            return;
        }

        open("/");
        $("input#longUrl").setValue("https://vr.fi");
        $("button#shortenIt").click();

        $("a#resultLink").shouldBe(visible);
        $("#resultLink").click();

        verifyThatVROpened();
    }

    @Test
    public void saveLinkAndCopyValueAndOpenIt() {
        boolean browserIsHtmlUnit = TestUtils.whichBrowser().equals(Selenide.Browser.HTMLUNIT);
        if (browserIsHtmlUnit) {
            Assume.assumeTrue("External resources not working with " + Selenide.Browser.HTMLUNIT + ". Test ignored",
                    true);
            return;
        }

        open("/");
        $("input#longUrl").setValue("https://vr.fi");
        $("button#shortenIt").click();

        $("a#resultLink").shouldBe(visible);
        String linkText = $("a#resultLink").getText();

        open(linkText);
        verifyThatVROpened();
    }

    @Test
    public void openSomethingNonExisting() {
        open("/perkele");
        $("h1").shouldBe(exist);
        $("h1").shouldHave(text("404"));
    }

    private void verifyThatVROpened() {
        SelenideElement vrLogo = $("a.mainLogo").find("img");
        vrLogo.shouldBe(exist);
        vrLogo.shouldHave(attribute("alt", "VR"));
    }
}
