package eu.yals.test.ui.home;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.ui.SelenideTest;
import eu.yals.test.utils.SelenideUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.pageobjects.HomePageObject.ErrorModal.ERROR_MODAL;
import static eu.yals.test.pageobjects.HomePageObject.MainArea.*;
import static eu.yals.test.pageobjects.HomePageObject.OverallArea.*;
import static eu.yals.test.pageobjects.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static eu.yals.test.pageobjects.HomePageObject.ResultArea.RESULT_AREA;
import static org.junit.Assert.fail;

/**
 * Checks state of front page (elements and so on...)
 *
 * @since 1.0
 */
@SpringBootTest
public class VisibleStateTest extends SelenideTest {
    @Before
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
    }

    @Test
    public void errorModalNotExistByDefault() {
        ERROR_MODAL.shouldNot(exist);
    }

    @Test
    public void mainAreaIsVisible() {
        MAIN_AREA.should(exist);
        MAIN_AREA.shouldBe(visible);
    }

    @Test
    public void overallAreaIsVisible() {
        OVERALL_AREA.should(exist);
        OVERALL_AREA.shouldBe(visible);
    }

    @Test
    public void resultAreaIsHidden() {
        RESULT_AREA.shouldNotBe(visible);
    }

    @Test
    public void qrCodeAreaIsHidden() {
        QR_CODE_AREA.shouldNotBe(visible);
    }

    @Test
    public void mainAreaHasFieldAndButton() {
        LONG_URL_INPUT.should(exist);
        SUBMIT_BUTTON.should(exist);
    }

    @Test
    public void formHasOnlyOneButton() {
        List<SelenideElement> buttons = MAIN_AREA.findAll("vaadin-button");
        Assert.assertEquals("Only 1 button expected", 1, buttons.size());
    }

    @Test
    public void inputAndButtonAreNotDisabled() {
        LONG_URL_INPUT.shouldBe(enabled);
        SUBMIT_BUTTON.shouldBe(enabled);
    }

    @Test
    public void inputShouldHavePlaceholder() {
        LONG_URL_INPUT.shouldHave(attribute("placeholder"));
    }

    @Test
    public void shouldHaveCorrectTitle() {
        Assert.assertEquals("Link shortener for friends", SelenideUtils.getPageTitle());
    }

    @Test
    public void mainDivShouldHaveH2() {
        TITLE.should(exist);
    }

    @Test
    public void inputFieldHasLabel() {
        LONG_URL_INPUT_LABEL.should(exist);
        String labelText = LONG_URL_INPUT_LABEL.getText();
        Assert.assertTrue(StringUtils.isNotBlank(labelText));
    }

    @Test
    public void buttonIsPrimaryAndHasText() {
        SUBMIT_BUTTON.shouldHave(attribute("theme", "primary"));
        SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        BANNER.should(exist);
        BANNER.shouldBe(visible);
        BANNER.shouldHave(text("public"));
    }

    @Test
    public void overallLinksTextExistsAndDisplayed() {
        OVERALL_LINKS_TEXT.should(exist);
        OVERALL_LINKS_TEXT.shouldBe(visible);
        OVERALL_LINKS_TEXT.shouldHave(text("Yals already saved"));
    }

    @Test
    public void overallLinksNumberExistsAndNumber() {
        OVERALL_LINKS_NUMBER.should(exist);
        OVERALL_LINKS_NUMBER.shouldBe(visible);

        String numberText = OVERALL_LINKS_NUMBER.getText();
        try {
            int numberOfSavedLinks = Integer.parseInt(numberText);
            Assert.assertTrue(numberOfSavedLinks >= 0);
        } catch (NumberFormatException e) {
            fail("Number of saved links is not a valid number");
        }
    }
}
