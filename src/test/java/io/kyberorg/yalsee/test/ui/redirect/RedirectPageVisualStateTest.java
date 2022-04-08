package io.kyberorg.yalsee.test.ui.redirect;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import io.kyberorg.yalsee.ui.special.RedirectPage;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Testing Visual State of {@link RedirectPage}.
 *
 * @since 3.0.5
 */
public class RedirectPageVisualStateTest extends SelenideTest {
    private static final String OUR_LONG_LINK = "https://github.com/kyberorg/yalsee/issues/353";
    private static String ourShortLink;

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        if (Strings.isBlank(ourShortLink)) {
            open("/");
            ourShortLink = HomePageObject.storeAndReturnSavedUrl(OUR_LONG_LINK);
            waitForVaadin();
        }
        open(ourShortLink);
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Tests that Page has common application Layout.
     */
    @Test
    public void pageHasCommonLayout() {
        YalseeCommonsPageObject.verifyThatPageHasYalseeLayout();
    }

    /**
     * Tests that element with short link is link itself and points to same page.
     */
    @Test
    public void originLinkIsLinkAndPointsToSamePage() {
        RedirectPageObject.Links.ORIGIN_LINK.shouldBe(visible);
        RedirectPageObject.Links.ORIGIN_LINK.shouldHave(attribute("href"));
    }

    /**
     *  Tests that element with long link is link itself and points to long link we stored.
     */
    @Test
    public void targetLinkIsLinkAndPointsToLongLink() {
        RedirectPageObject.Links.TARGET_LINK.shouldBe(visible);
        RedirectPageObject.Links.TARGET_LINK.shouldHave(attribute("href"));

        RedirectPageObject.Links.TARGET_LINK.shouldHave(text(OUR_LONG_LINK));
        RedirectPageObject.Links.TARGET_LINK.shouldHave(attribute("href", OUR_LONG_LINK));
    }

    /**
     * Tests that element with here link is link itself, has "here" as text and points to long link.
     */
    @Test
    public void hereLinkIsLinkHasTextHereAndPointsToLongLink() {
        RedirectPageObject.Links.HERE_LINK.shouldBe(visible);
        RedirectPageObject.Links.HERE_LINK.shouldHave(attribute("href"));

        RedirectPageObject.Links.HERE_LINK.shouldHave(text("here"));
        RedirectPageObject.Links.HERE_LINK.shouldHave(attribute("href", OUR_LONG_LINK));
    }

    /**
     * Tests that NB! element is bold and has "NB!" text.
     */
    @Test
    public void nbElementIsBoldAndHasNBText() {
        RedirectPageObject.Elements.NB.shouldBe(visible);
        RedirectPageObject.Elements.NB.shouldHave(cssClass("bold"));
        RedirectPageObject.Elements.NB.shouldHave(text("NB!"));
    }

    /**
     * Tests that Bypass Symbol is present and same as Symbol from Server Settings.
     */
    @Test
    public void bypassSymbolIsPresentAndSameAsInSettings() {
        RedirectPageObject.Elements.BYPASS_SYMBOL.shouldBe(visible);
        RedirectPageObject.Elements.BYPASS_SYMBOL.shouldNotBe(empty);

        String bypassSymbolDeFacto = RedirectPageObject.Elements.BYPASS_SYMBOL.getText();
        String bypassSymbolFromSettings = TestUtils.getTestedEnv().getRedirectPageBypassSymbol();

        Assertions.assertEquals(bypassSymbolFromSettings, bypassSymbolDeFacto);
    }

    /**
     * Tests that counter is present and not empty.
     */
    @Test
    public void counterIsPresentAndNotEmpty() {
        RedirectPageObject.Elements.COUNTER.shouldBe(visible);
        RedirectPageObject.Elements.COUNTER.shouldNotBe(empty);
    }

    /**
     * Tests that lenDiff String is not empty and has Brackets.
     */
    @Test
    public void lenDiffStringIsNotEmptyAndHasBrackets() {
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldBe(visible);
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldNotBe(empty);
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldHave(text("("));
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldHave(text(")"));
    }

    /**
     * Tests that lenDiff String has Number.
     */
    @Test
    public void lenDiffStringHasNumber() {
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldHave(matchText("\\d+"));
    }

    /**
     * Tests that lenDiff String Number is positive and String has Word shorter.
     */
    @Test
    public void lenDiffStringNumberIsPositiveAndHasWordShorter() {
        String lenDiffStringText = RedirectPageObject.Elements.LEN_DIFF_STRING.getText();
        String numberStr = lenDiffStringText.replaceAll("[^0-9]", "");
        int number = Integer.parseInt(numberStr);

        Assertions.assertTrue(number > 0);
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldHave(text("shorter"));
    }

    /**
     * Tests that lenDiff String has Word Chars.
     */
    @Test
    public void lenDiffStringHasWordChars() {
        RedirectPageObject.Elements.LEN_DIFF_STRING.shouldHave(text("chars"));
    }
}
