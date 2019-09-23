package eu.yals.test.ui.pages.front;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.FrontPage;
import eu.yals.test.ui.pageobjects.external.Wikipedia;
import eu.yals.test.utils.Selenide;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

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

        FrontPage.ErrorRow.ERROR_CLOSE.click();
        FrontPage.ErrorRow.ERROR_MODAL.shouldNotBe(visible);

    }

    @Test
    public void closeButtonClosesErrorModalButNotRemoves() {
        pasteValueInFormAndSubmitIt(" ");

        FrontPage.ErrorRow.ERROR_CLOSE.click();
        FrontPage.ErrorRow.ERROR_MODAL.shouldNotBe(visible);
        FrontPage.ErrorRow.ERROR_MODAL.shouldBe(exist);
    }

    @Test
    public void shortenItButtonClearsResultAndValueIfVisible() {
        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");
        FrontPage.ResultRow.RESULT_DIV.shouldBe(visible);
        FrontPage.MainRow.LONG_URL_INPUT.shouldBe(empty);

        pasteValueInFormAndSubmitIt("g&%g");
        FrontPage.ResultRow.RESULT_DIV.shouldNotBe(visible);
        FrontPage.ResultRow.RESULT_LINK.shouldBe(empty);

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

        FrontPage.ResultRow.RESULT_DIV.shouldBe(visible);
        FrontPage.ResultRow.COPY_RESULT_ICON.shouldBe(visible);

        FrontPage.ResultRow.COPY_RESULT_ICON.click();
        FrontPage.MainRow.LONG_URL_INPUT.click();
        FrontPage.MainRow.LONG_URL_INPUT.sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String shortLink = FrontPage.ResultRow.RESULT_LINK.text();

        String pastedLink = FrontPage.MainRow.LONG_URL_INPUT.val();
        Assert.assertEquals(shortLink, pastedLink);
    }

    @Test
    public void linksCounterIncreasedValueAfterSave() {
        long initialNumber = Long.parseLong(FrontPage.OverallRow.OVERALL_LINKS_NUMBER.text());

        pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        long numberAfterLinkSaved = Long.parseLong(FrontPage.OverallRow.OVERALL_LINKS_NUMBER.text());
        Assert.assertEquals(initialNumber + 1, numberAfterLinkSaved);
    }

    @Test
    public void saveAndRetrieveLinkFromRussianWikipedia() {
        pasteValueInFormAndSubmitIt("https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8");

        String shortLink = FrontPage.ResultRow.RESULT_LINK.text();
        open(shortLink);

        SelenideElement articleTitle = Wikipedia.getArticleTitle();
        articleTitle.should(exist);
        articleTitle.shouldHave(text(Wikipedia.ARTICLE_TITLE));
    }
}
