package ee.yals.test.selenide.slash;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.selenide.UITest;
import org.junit.Ignore;
import org.junit.Rule;
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
    @Rule // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenShotsOnFail = ScreenShooter.failedTests();

    @Test
    public void urlWithJustSlashWillOpenFrontPage() {
        open("/");
        $("#longUrl").shouldBe(exist);
        $("#shortenIt").shouldBe(exist);
    }

    @Test
    @Ignore //not working with HtmlUnit
    public void saveLinkAndClickOnResult() {
        open("/");
        $("input#longUrl").setValue("https://vr.fi");
        $("button#shortenIt").click();

        $("a#resultLink").shouldBe(visible);
        $("#resultLink").click();

        verifyThatVROpened();
    }

    @Test
    @Ignore //not working with HtmlUnit
    public void saveLinkAndCopyValueAndOpenIt() {
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
