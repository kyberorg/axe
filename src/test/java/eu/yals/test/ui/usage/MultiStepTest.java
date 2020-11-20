package eu.yals.test.ui.usage;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.pageobjects.HomePageObject;
import eu.yals.test.pageobjects.external.Eki;
import eu.yals.test.pageobjects.external.Wikipedia;
import eu.yals.test.ui.SelenideTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.pageobjects.HomePageObject.ErrorModal.ERROR_BUTTON;
import static eu.yals.test.pageobjects.HomePageObject.ErrorModal.ERROR_MODAL;
import static eu.yals.test.pageobjects.HomePageObject.MainArea.LONG_URL_INPUT;
import static eu.yals.test.pageobjects.HomePageObject.ResultArea.*;

/**
 * Contains multi step tests for Front page
 *
 * @since 1.0
 */
@SpringBootTest
public class MultiStepTest extends SelenideTest {
    @Before
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        updateTestNameAndStartVideo();
    }

    @Test
    public void closeButtonReallyClosesErrorNotification() {
        HomePageObject.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");
        ERROR_MODAL.shouldBe(visible);
        ERROR_BUTTON.should(exist);
        ERROR_BUTTON.click();
        ERROR_MODAL.shouldNot(exist);
    }

    //@Test
    //TODO re-enable when #232 is fixed
    public void shortenItButtonClearsResultAndValueIfVisible() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        RESULT_AREA.shouldBe(visible);
        LONG_URL_INPUT.shouldBe(empty);

        HomePageObject.pasteValueInFormAndSubmitIt("g&%g");
        RESULT_AREA.shouldNotBe(visible);
        RESULT_LINK.shouldBe(empty);
    }

    /**
     * Tests copy link button
     */
    @Test
    public void copyLinkButtonShouldCopyShortLink() {
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

        RESULT_AREA.shouldBe(visible);
        COPY_LINK_ICON.shouldBe(visible);

        COPY_LINK_ICON.click();
        LONG_URL_INPUT.click();

        LONG_URL_INPUT.sendKeys(Keys.chord(Keys.CONTROL, "v"));

        String shortLink = RESULT_LINK.text();
        String pastedLink = LONG_URL_INPUT.val();

        Assert.assertEquals(shortLink, pastedLink);
    }

    @Test
    public void linksCounterIncreasedValueAfterSave() throws InterruptedException {
        long initialNumber = HomePageObject.getNumberOfSavedLinks();

        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");
        //sometime it takes time to update counter. Waiting 5 seconds to prevent flaky test.
        TimeUnit.SECONDS.sleep(5);

        long numberAfterLinkSaved = HomePageObject.getNumberOfSavedLinks();

        //+1 logic is no longer valid, because someone else (i.e. other tests) can also store link within same time
        Assert.assertTrue("Number Before: " + initialNumber + " Number after: " + numberAfterLinkSaved,
                numberAfterLinkSaved > initialNumber);
    }

    @Test
    public void saveAndRetrieveLinkFromRussianWikipedia() {
        HomePageObject.pasteValueInFormAndSubmitIt(
                "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8");

        open(HomePageObject.getSavedUrl());

        SelenideElement articleTitle = $(Wikipedia.getArticleTitle());
        articleTitle.should(exist);
        articleTitle.shouldHave(text(Wikipedia.ARTICLE_TITLE));
    }

    @Test
    public void linkWithEstonianLettersMustBeSavedAndReused() {
        HomePageObject.pasteValueInFormAndSubmitIt("http://eki.ee/dict/ekss/index.cgi?Q=l%C3%A4bi%20tulema");

        open(HomePageObject.getSavedUrl());
        SelenideElement titleSpan = $(Eki.getTitle());
        titleSpan.should(exist);
        titleSpan.shouldHave(text(Eki.TITLE_TEXT));
    }
}
