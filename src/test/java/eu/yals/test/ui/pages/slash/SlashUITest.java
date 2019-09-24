package eu.yals.test.ui.pages.slash;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.TestUtils;
import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.Page404;
import eu.yals.test.ui.pageobjects.external.VR;
import eu.yals.test.utils.Selenide;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.ui.pageobjects.FrontPage.MainRow.LONG_URL_INPUT;
import static eu.yals.test.ui.pageobjects.FrontPage.MainRow.SUBMIT_BUTTON;
import static eu.yals.test.ui.pageobjects.FrontPage.ResultRow.RESULT_LINK;

/**
 * Testing /(Slash) URL
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SlashUITest extends UITest {

    @Test
    public void urlWithJustSlashWillOpenFrontPage() {
        open("/");
        LONG_URL_INPUT.shouldBe(exist);
        SUBMIT_BUTTON.shouldBe(exist);
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
        pasteValueInFormAndSubmitIt("https://vr.fi");

        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.click();

        verifyThatVROpened();
    }

    @Test
    public void saveLinkAndCopyValueAndOpenIt() {
        if (isBrowserHtmlUnit()) {
            Assume.assumeTrue("External resources not working with " + Selenide.Browser.HTMLUNIT + ". Test ignored",
                    true);
            return;
        }

        open("/");
        pasteValueInFormAndSubmitIt("https://vr.fi");

        RESULT_LINK.shouldBe(visible);
        String linkText = RESULT_LINK.getText();

        open(linkText);
        verifyThatVROpened();
    }

    @Test
    public void openSomethingNonExisting() {
        open("/perkele");
        Page404.H1.shouldBe(exist);
        Page404.H1.shouldHave(text("404"));
    }

    @Test
    public void openSomethingNonExistingDeeperThanSingleLevel() {
        open("/void/something/here");
        Page404.H1.shouldBe(exist);
        Page404.H1.shouldHave(text("404"));
    }

    private void verifyThatVROpened() {
        SelenideElement vrLogo = VR.LOGO.find("img");
        vrLogo.shouldBe(exist);
        vrLogo.shouldHave(attribute("alt", "VR"));
    }
}
