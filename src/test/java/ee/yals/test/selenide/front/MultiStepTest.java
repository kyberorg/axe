package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import ee.yals.test.utils.Selenide;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.selectors.FrontSelectors.ErrorRow.ERROR_CLOSE;
import static ee.yals.test.utils.selectors.FrontSelectors.ErrorRow.ERROR_MODAL;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.selectors.FrontSelectors.OverallRow.OVERALL_LINKS_NUMBER;
import static ee.yals.test.utils.selectors.FrontSelectors.ResultRow.*;

/**
 * Contains multi step tests for Front page
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MultiStepTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void closeButtonReallyClosesErrorModal() {
        pasteValueInFormAndSubmitIt(" ");

        ERROR_CLOSE.click();
        ERROR_MODAL.shouldNotBe(visible);

    }

    @Test
    public void closeButtonClosesErrorModalButNotRemoves() {
        pasteValueInFormAndSubmitIt(" ");

        ERROR_CLOSE.click();
        ERROR_MODAL.shouldNotBe(visible);
        ERROR_MODAL.shouldBe(exist);
    }

    @Test
    public void shortenItButtonClearsResultAndValueIfVisible() {
        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");
        RESULT_DIV.shouldBe(visible);
        LONG_URL_INPUT.shouldBe(empty);

        pasteValueInFormAndSubmitIt("ggg");
        RESULT_DIV.shouldNotBe(visible);
        RESULT_LINK.shouldBe(empty);

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

        RESULT_DIV.shouldBe(visible);
        COPY_RESULT_ICON.shouldBe(visible);

        COPY_RESULT_ICON.click();
        LONG_URL_INPUT.click();
        LONG_URL_INPUT.sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String shortLink = RESULT_LINK.text();

        String pastedLink = LONG_URL_INPUT.val();
        Assert.assertEquals(shortLink, pastedLink);
    }

    @Test
    public void linksCounterIncreasedValueAfterSave() {
        long initialNumber = Long.parseLong(OVERALL_LINKS_NUMBER.text());

        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        long numberAfterLinkSaved = Long.parseLong(OVERALL_LINKS_NUMBER.text());
        Assert.assertEquals(initialNumber + 1, numberAfterLinkSaved);
    }

}
