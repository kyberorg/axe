package io.kyberorg.yalsee.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.SelenideUtils;
import io.kyberorg.yalsee.test.utils.browser.BrowserSize;
import io.kyberorg.yalsee.test.utils.browser.BrowserUtils;
import io.kyberorg.yalsee.ui.HomeView;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.utils.browser.BrowserUtils.EXTRA_SMALL_SCREEN_MAX_WIDTH_PIXELS;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Checks state of front page (elements and so on...).
 *
 * @since 1.0
 */
@SpringBootTest
public class VisibleStateTest extends SelenideTest {
    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        waitForVaadin();
    }

    /**
     * Tests that error modal not exist, when fresh page occurs.
     */
    @Test
    public void errorModalNotExistByDefault() {
        HomePageObject.ErrorModal.ERROR_MODAL.shouldNot(exist);
    }

    /**
     * Tests that area with inputs/buttons etc is visible.
     */
    @Test
    public void mainAreaIsVisible() {
        HomePageObject.MainArea.MAIN_AREA.should(exist);
        HomePageObject.MainArea.MAIN_AREA.shouldBe(visible);
    }

    /**
     * Tests that area with overall info exists and visible.
     */
    @Test
    public void overallAreaIsVisible() {
        HomePageObject.OverallArea.OVERALL_AREA.should(exist);
        HomePageObject.OverallArea.OVERALL_AREA.shouldBe(visible);
    }

    /**
     * Tests that by default result area is not visible.
     */
    @Test
    public void resultAreaIsHidden() {
        HomePageObject.ResultArea.RESULT_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that by default area with QR Code is hidden.
     */
    @Test
    public void qrCodeAreaIsHidden() {
        HomePageObject.QrCodeArea.QR_CODE_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that by default area with My Links Note Area is hidden.
     */
    @Test
    public void myLinksNoteAreaIsHidden() {
        HomePageObject.QrCodeArea.QR_CODE_AREA.shouldNotBe(visible);
    }

    /**
     * Tests that main area has input and button.
     */
    @Test
    public void mainAreaHasFieldAndButton() {
        HomePageObject.MainArea.LONG_URL_INPUT.should(exist);
        HomePageObject.MainArea.SUBMIT_BUTTON.should(exist);
    }

    /**
     * Tests main area form has one and only one button.
     */
    @Test
    public void formHasOnlyOneButton() {
        List<SelenideElement> buttons = HomePageObject.MainArea.MAIN_AREA.findAll("vaadin-button");
        assertEquals(1, buttons.size(), "Only 1 button expected");
    }

    /**
     * Tests that both input and button are enabled.
     */
    @Test
    public void inputAndButtonAreNotDisabled() {
        HomePageObject.MainArea.LONG_URL_INPUT.shouldBe(enabled);
        HomePageObject.MainArea.SUBMIT_BUTTON.shouldBe(enabled);
    }

    /**
     * Tests input, if it has placeholder attribute.
     */
    @Test
    public void inputShouldHavePlaceholder() {
        HomePageObject.MainArea.LONG_URL_INPUT.shouldHave(attribute("placeholder"));
    }

    /**
     * Tests page title.
     */
    @Test
    public void shouldHaveCorrectTitle() {
        assertEquals("Yalsee - the link shortener", SelenideUtils.getPageTitle());
    }

    /**
     * Tests title in main area.
     */
    @Test
    public void mainDivShouldHaveTitle() {
        HomePageObject.MainArea.TITLE.should(exist);
    }

    /**
     * Tests main area title's tag.
     */
    @Test
    public void titleShouldBeH2() {
        assertEquals("h2", HomePageObject.MainArea.TITLE.getTagName().toLowerCase());
    }

    /**
     * Tests that title has word "long" if screen large or has not if screen is extra small (i.e phone).
     */
    @Test
    public void titleShouldContainWordLong() {
        BrowserSize browserSize = BrowserUtils.getBrowserSize();
        if (browserSize.getWidth() > EXTRA_SMALL_SCREEN_MAX_WIDTH_PIXELS) {
            HomePageObject.MainArea.TITLE.shouldHave(text("long"));
        } else {
            HomePageObject.MainArea.TITLE.shouldNot(have(text("long")));
        }
    }

    /**
     * Tests main area title CSS consideration.
     */
    @Test
    public void titleShouldHaveNoExtraSpaceBelow() {
        HomePageObject.MainArea.TITLE.shouldHave(cssClass("compact-title"));
    }

    /**
     * Tests main area title's wording.
     */
    @Test
    public void titleShouldHaveWordsLinksAndShort() {
        HomePageObject.MainArea.TITLE.shouldHave(text("links"));
        HomePageObject.MainArea.TITLE.shouldHave(text("short"));
    }

    /**
     * Tests that input goes right after title.
     */
    @Test
    public void inputFieldShouldBeAfterTitle() {
        SelenideElement nextElement = HomePageObject.MainArea.TITLE.sibling(0);
        nextElement.should(exist);
        nextElement.shouldHave(id(HomeView.IDs.INPUT));
    }

    /**
     * Tests that input has label.
     */
    @Test
    public void inputFieldHasLabel() {
        HomePageObject.MainArea.LONG_URL_INPUT_LABEL.should(exist);
        String labelText = HomePageObject.MainArea.LONG_URL_INPUT_LABEL.getText();
        Assertions.assertTrue(StringUtils.isNotBlank(labelText));
    }

    /**
     * Tests that Button is primary button and has text.
     */
    @Test
    public void buttonIsPrimaryAndHasText() {
        HomePageObject.MainArea.SUBMIT_BUTTON.shouldHave(attribute("theme", "primary"));
        HomePageObject.MainArea.SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    /**
     * Tests that public access banner is there and contains needed wording.
     */
    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        HomePageObject.MainArea.BANNER.should(exist);
        HomePageObject.MainArea.BANNER.shouldBe(visible);
        HomePageObject.MainArea.BANNER.shouldHave(text("public"));
    }

    /**
     * Test that text about overall links saved is exists, visible and has correct wording.
     */
    @Test
    public void overallLinksTextExistsAndDisplayed() {
        HomePageObject.OverallArea.OVERALL_LINKS_TEXT.should(exist);
        HomePageObject.OverallArea.OVERALL_LINKS_TEXT.shouldBe(visible);
        HomePageObject.OverallArea.OVERALL_LINKS_TEXT.shouldHave(text("Yalsee already saved"));
    }

    /**
     * Test that text about overall links saved has number.
     */
    @Test
    public void overallLinksNumberExistsAndNumber() {
        HomePageObject.OverallArea.OVERALL_LINKS_NUMBER.should(exist);
        HomePageObject.OverallArea.OVERALL_LINKS_NUMBER.shouldBe(visible);

        String numberText = HomePageObject.OverallArea.OVERALL_LINKS_NUMBER.getText();
        try {
            int numberOfSavedLinks = Integer.parseInt(numberText);
            Assertions.assertTrue(numberOfSavedLinks >= 0);
        } catch (NumberFormatException e) {
            Assertions.fail("Number of saved links is not a valid number");
        }
    }
}
