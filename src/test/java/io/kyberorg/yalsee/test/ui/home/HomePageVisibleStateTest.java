package io.kyberorg.yalsee.test.ui.home;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.browser.BrowserSize;
import io.kyberorg.yalsee.test.utils.browser.BrowserUtils;
import io.kyberorg.yalsee.ui.pages.home.HomePage;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.utils.browser.BrowserUtils.EXTRA_SMALL_SCREEN_MAX_WIDTH_PIXELS;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Checks state of front page (elements and so on...).
 *
 * @since 1.0
 */
public class HomePageVisibleStateTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Tests that error modal not exist, when fresh page occurs.
     */
    @Test
    public void errorModalNotExistByDefault() {
        ErrorModal.ERROR_MODAL.shouldNot(exist);
    }

    /**
     * Tests that area with inputs/buttons etc. is visible.
     */
    @Test
    public void mainAreaIsVisible() {
        MainArea.MAIN_AREA.should(exist);
        MainArea.MAIN_AREA.shouldBe(visible);
    }

    /**
     * Tests that area with overall info exists and visible.
     */
    @Test
    public void overallAreaIsVisible() {
        OverallArea.OVERALL_AREA.should(exist);
        OverallArea.OVERALL_AREA.shouldBe(visible);
    }

    /**
     * Tests that by default result area is not visible.
     */
    @Test
    public void resultAreaIsHidden() {
        ResultArea.RESULT_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that by default area with QR Code is hidden.
     */
    @Test
    public void qrCodeAreaIsHidden() {
        QrCodeArea.QR_CODE_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that by default area with My Links Note Area is hidden.
     */
    @Test
    public void myLinksNoteAreaIsHidden() {
        MyLinksNoteArea.MY_LINKS_NOTE_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that main area has input and button.
     */
    @Test
    public void mainAreaHasFieldAndButton() {
        MainArea.LONG_URL_INPUT.should(exist);
        MainArea.SUBMIT_BUTTON.should(exist);
    }

    /**
     * Tests main area form has one and only one button.
     */
    @Test
    public void formHasOnlyOneButton() {
        List<SelenideElement> buttons = MainArea.MAIN_AREA.findAll("vaadin-button");
        assertEquals(1, buttons.size(), "Only 1 button expected");
    }

    /**
     * Tests that both input and button are enabled.
     */
    @Test
    public void inputAndButtonAreNotDisabled() {
        MainArea.LONG_URL_INPUT.shouldBe(enabled);
        MainArea.SUBMIT_BUTTON.shouldBe(enabled);
    }

    /**
     * Tests input, if it has placeholder attribute.
     */
    @Test
    public void inputShouldHavePlaceholder() {
        MainArea.LONG_URL_INPUT.shouldHave(attribute("placeholder"));
    }

    /**
     * Tests that Protocol Selector is not visible by default.
     */
    @Test
    public void protocolSelectorNotVisibleByDefault() {
        MainArea.ProtocolSelector.SELECTOR.shouldNotBe(visible);
    }

    /**
     * Protocol Selector has all needed elements.
     */
    @Test
    public void protocolSelectorHasAllNeededElements() {
        HomePageObject.pasteValueInFormAndSubmitIt("kv.ee");
        MainArea.ProtocolSelector.SELECTOR.shouldBe(visible);
        MainArea.ProtocolSelector.LABEL.shouldBe(visible);
        MainArea.ProtocolSelector.LABEL.shouldHave(text("Protocol"));
        MainArea.ProtocolSelector.ERROR_MESSAGE.shouldNotBe(visible);
    }

    /**
     * Protocol Selector has 3 options, they all visible and have needed text.
     */
    @Test
    public void protocolSelectorHasThreeOptions() {
        MainArea.ProtocolSelector.OPTIONS.shouldHave(size(App.THREE));
        MainArea.ProtocolSelector.HTTPS_OPTION.shouldBe(visible);
        MainArea.ProtocolSelector.HTTPS_OPTION.shouldHave(text("https://"));
        MainArea.ProtocolSelector.HTTP_OPTION.shouldBe(visible);
        MainArea.ProtocolSelector.HTTP_OPTION.shouldHave(text("http://"));
        MainArea.ProtocolSelector.FTP_OPTION.shouldBe(visible);
        MainArea.ProtocolSelector.FTP_OPTION.shouldHave(text("ftp://"));
    }

    /**
     * Tests that Description Accordion exists and visible.
     */
    @Test
    public void descriptionAccordionExistsAndVisible() {
        MainArea.DESCRIPTION_ACCORDION.should(exist);
        MainArea.DESCRIPTION_ACCORDION.shouldBe(visible);
    }

    /**
     * Tests that Description Accordion closed by default.
     */
    @Test
    public void descriptionAccordionClosedByDefault() {
        MainArea.DESCRIPTION_INPUT.shouldNotBe(visible);
    }

    /**
     * Tests that Description Accordion has Words Description and Optional.
     */
    @Test
    public void descriptionAccordionHasWordsDescriptionAndOptional() {
        MainArea.DESCRIPTION_ACCORDION.shouldHave(text("Description"));
        MainArea.DESCRIPTION_ACCORDION.shouldHave(text("optional"));
    }

    /**
     * Tests that Description Input is hidden by default.
     */
    @Test
    public void descriptionInputIsHiddenByDefault() {
        MainArea.DESCRIPTION_INPUT_ELEMENT.shouldBe(hidden);
    }

    /**
     * Tests page title.
     */
    @Test
    public void shouldHaveCorrectTitle() {
        assertEquals("Yalsee - the link shortener", Selenide.title());
    }

    /**
     * Tests title in main area.
     */
    @Test
    public void mainDivShouldHaveTitle() {
        MainArea.TITLE.should(exist);
    }

    /**
     * Tests main area title's tag.
     */
    @Test
    public void titleShouldBeH2() {
        assertEquals("h2", MainArea.TITLE.getTagName().toLowerCase());
    }

    /**
     * Tests that title has word "long" if screen large or has not if screen is extra small (i.e. phone).
     */
    @Test
    public void titleShouldContainWordLong() {
        BrowserSize browserSize = BrowserUtils.getBrowserSize();
        if (browserSize.getWidth() > EXTRA_SMALL_SCREEN_MAX_WIDTH_PIXELS) {
            MainArea.TITLE.shouldHave(text("long"));
        } else {
            MainArea.TITLE.shouldNot(have(text("long")));
        }
    }

    /**
     * Tests main area title CSS consideration.
     */
    @Test
    public void titleShouldHaveNoExtraSpaceBelow() {
        MainArea.TITLE.shouldHave(cssClass("compact-title"));
    }

    /**
     * Tests main area title's wording.
     */
    @Test
    public void titleShouldHaveWordsLinksAndShort() {
        MainArea.TITLE.shouldHave(text("links"));
        MainArea.TITLE.shouldHave(text("short"));
    }

    /**
     * Tests that input goes right after title.
     */
    @Test
    public void inputFieldShouldBeAfterTitle() {
        SelenideElement nextElement = MainArea.TITLE.sibling(0);
        nextElement.should(exist);
        nextElement.shouldHave(id(HomePage.IDs.INPUT));
    }

    /**
     * Tests that input has label.
     */
    @Test
    public void inputFieldHasLabel() {
        MainArea.LONG_URL_INPUT_LABEL.should(exist);
        String labelText = MainArea.LONG_URL_INPUT_LABEL.getText();
        Assertions.assertTrue(StringUtils.isNotBlank(labelText));
    }

    /**
     * Tests that Button is primary button and has text.
     */
    @Test
    public void buttonIsPrimaryAndHasText() {
        MainArea.SUBMIT_BUTTON.shouldHave(attribute("theme", "primary"));
        MainArea.SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    /**
     * Tests that public access banner is there and contains needed wording.
     */
    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        MainArea.BANNER.should(exist);
        MainArea.BANNER.shouldBe(visible);
        MainArea.BANNER.shouldHave(text("public"));
    }

    /**
     * Test that text about overall links saved is exists, visible and has correct wording.
     */
    @Test
    public void overallLinksTextExistsAndDisplayed() {
        OverallArea.OVERALL_LINKS_TEXT.should(exist);
        OverallArea.OVERALL_LINKS_TEXT.shouldBe(visible);
        OverallArea.OVERALL_LINKS_TEXT.shouldHave(text("Yalsee already saved"));
    }

    /**
     * Test that text about overall links saved has number.
     */
    @Test
    public void overallLinksNumberExistsAndNumber() {
        OverallArea.OVERALL_LINKS_NUMBER.should(exist);
        OverallArea.OVERALL_LINKS_NUMBER.shouldBe(visible);

        String numberText = OverallArea.OVERALL_LINKS_NUMBER.getText();
        try {
            int numberOfSavedLinks = Integer.parseInt(numberText);
            Assertions.assertTrue(numberOfSavedLinks >= 0);
        } catch (NumberFormatException e) {
            Assertions.fail("Number of saved links is not a valid number");
        }
    }
}
