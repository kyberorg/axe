package pm.axe.test.ui.usage;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.NotFoundViewPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.pageobjects.external.Eki;
import pm.axe.test.pageobjects.external.Wikipedia;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import org.openqa.selenium.Keys;
import pm.axe.test.pageobjects.VaadinPageObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contains multistep tests for Front page.
 *
 * @since 1.0
 */
public class MultiStepTest extends SelenideTest {

    /**
     * Timeout to prevent flaky tests in seconds.
     */
    private static final int TIMEOUT_TO_PREVENT_FLAKY_TESTS = 5;

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        VaadinPageObject.waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Close Button at Error Box closes it.
     */
    @Test
    public void closeButtonReallyClosesErrorNotification() {
        HomePageObject.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");
        HomePageObject.ErrorModal.ERROR_MODAL.shouldBe(visible);
        HomePageObject.ErrorModal.ERROR_BUTTON.should(exist);
        HomePageObject.ErrorModal.ERROR_BUTTON.click();
        HomePageObject.ErrorModal.ERROR_MODAL.shouldNot(exist);
    }

    /**
     * Submit button hides result area and clear its values.
     * Currently, not working as it should. Bug filed.
     */
    //@Test
    //TODO re-enable when #232 is fixed
    public void shortenItButtonClearsResultAndValueIfVisible() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/axe");

        HomePageObject.ResultArea.RESULT_AREA.shouldBe(visible);
        HomePageObject.MainArea.LongURLInput.INPUT.shouldBe(empty);

        HomePageObject.pasteValueInFormAndSubmitIt("g&%g");
        HomePageObject.ResultArea.RESULT_AREA.shouldNotBe(visible);
        HomePageObject.ResultArea.RESULT_LINK.shouldBe(empty);
    }

    /**
     * Tests copy link button.
     */
    @Test
    public void copyLinkButtonShouldCopyShortLink() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/axe");

        HomePageObject.ResultArea.RESULT_AREA.shouldBe(visible);
        HomePageObject.ResultArea.COPY_LINK_ICON.shouldBe(visible);

        HomePageObject.ResultArea.COPY_LINK_ICON.click();
        HomePageObject.MainArea.LongURLInput.INPUT.click();

        HomePageObject.MainArea.LongURLInput.INPUT.sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String shortLink = HomePageObject.ResultArea.RESULT_LINK.text();
        String pastedLink = HomePageObject.MainArea.LongURLInput.INPUT.val();

        assertEquals(shortLink, pastedLink);
    }

    /**
     * Counter value should increase after link saved.
     *
     * @throws InterruptedException when waiting fails
     */
    @Test
    public void linksCounterIncreasedValueAfterSave() throws InterruptedException {
        long initialNumber = HomePageObject.getNumberOfSavedLinks();

        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/axe");
        //sometimes it takes time to update counter. Waiting 5 seconds to prevent flaky test.
        TimeUnit.SECONDS.sleep(TIMEOUT_TO_PREVENT_FLAKY_TESTS);

        long numberAfterLinkSaved = HomePageObject.getNumberOfSavedLinks();

        //+1 logic is no longer valid, because someone else (i.e. other tests) can also store link within same time
        assertTrue(numberAfterLinkSaved > initialNumber,
                "Number Before: " + initialNumber + " Number after: " + numberAfterLinkSaved);
    }

    /**
     * Saves and retrieves link for russian wiki (tests encoding/decoding).
     */
    @Test
    public void saveAndRetrieveLinkFromRussianWikipedia() {
        HomePageObject.pasteValueInFormAndSubmitIt(
                "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_"
                        + "%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9"
                        + "_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_"
                        + "%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8"
                        + "%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_"
                        + "%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8");

        Selenide.open(HomePageObject.getSavedUrl() + TestUtils.addRedirectPageBypassSymbol());

        SelenideElement articleTitle = Selenide.$(Wikipedia.getArticleTitle());
        articleTitle.should(exist);
        articleTitle.shouldHave(text(Wikipedia.ARTICLE_TITLE));
    }

    /**
     * Saves and retrieves link with estonian letters.
     */
    @Test
    public void linkWithEstonianLettersMustBeSavedAndReused() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://eki.ee/dict/ekss/index.cgi?Q=l%C3%A4bi%20tulema");

        Selenide.open(HomePageObject.getSavedUrl() + TestUtils.addRedirectPageBypassSymbol());
        SelenideElement titleSpan = Selenide.$(Eki.getTitle());
        titleSpan.should(exist);
        titleSpan.shouldHave(text(Eki.TITLE_TEXT));
    }

    /**
     * Page 404 (aka Link Not Found Page) should open if Link used in wrong case.
     * I.E. <a href="https://axe.pm/abcdef">https://axe.pm/abcdef</a> and
     * <a href="https://axe.pm/ABCDEF">https://axe.pm/ABCDEF</a> should not lead to same URL.
     */
    @Issue("https://github.com/kyberorg/axe/issues/611")
    @Test
    public void page404ShouldOpenIfLinkUsedInWrongCase() {
        String shortUrl =
                HomePageObject.storeAndReturnSavedUrl("https://github.com/kyberorg/axe/issues/611");
        String ident = shortUrl.replace(TestUtils.getAppShortUrl() + "/", "");
        String bigIdent = ident.toUpperCase(Locale.ROOT);
        open(TestUtils.getAppShortUrl() + "/" + bigIdent + TestUtils.addRedirectPageBypassSymbol());
        expectPage404();
    }

    private void expectPage404() {
        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }
}
