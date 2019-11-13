package eu.yals.test.ui.pages.front;


import com.codeborne.selenide.SelenideElement;
import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.FrontPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static org.junit.Assert.fail;

/**
 * Checks state of front page (elements and so on...)
 *
 * @since 1.0
 */

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VisibleStateTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void errorBlockIsHidden() {
        FrontPage.ErrorRow.ERROR_MODAL.shouldNotBe(visible);
    }

    @Test
    public void mainBlockIsVisible() {
        FrontPage.MainRow.MAIN_AREA.shouldBe(visible);
    }

    @Test
    public void resultBlockIsHidden() {
        FrontPage.ResultRow.RESULT_DIV.shouldNotBe(visible);
    }

    @Test
    public void qrCodeBlockIsHidden() {
        FrontPage.QRCodeRow.QR_CODE_DIV.shouldNotBe(visible);
    }

    @Test
    public void mainAreaHasFieldAndButton() {
        SelenideElement inputField = FrontPage.MainRow.MAIN_AREA.find("vaadin-text-field");
        inputField.shouldBe(exist);

        SelenideElement button = FrontPage.MainRow.MAIN_AREA.find("vaadin-button");
        button.shouldBe(exist);
    }

    @Test
    public void formHasOnlyOneButton() {
        FrontPage.MainRow.MAIN_AREA.findAll("button").shouldHaveSize(1);
    }

    @Test
    public void inputAndButtonAreNotDisabled() {
        FrontPage.MainRow.LONG_URL_INPUT.shouldNotBe(disabled);
        FrontPage.MainRow.SUBMIT_BUTTON.shouldNotBe(disabled);
    }

    @Test
    public void inputShouldHavePlaceholder() {
        FrontPage.MainRow.LONG_URL_INPUT.shouldHave(attribute("placeholder"));
    }

    @Test
    public void shouldHaveCorrectTitle() {
        String title = title();
        Assert.assertEquals("Link shortener for friends", title);
    }

    @Test
    public void mainDivShouldHaveH2() {
        FrontPage.MainRow.H2.shouldBe(exist);
    }

    @Test
    public void inputFieldHasLabel() {
        SelenideElement label = FrontPage.MainRow.LONG_URL_INPUT.parent().find("label");
        label.shouldBe(exist);
        label.shouldNotBe(empty);
        label.shouldHave(attribute("for", "longUrl"));
    }

    @Test
    public void buttonIsPrimaryAndHasText() {
        FrontPage.MainRow.SUBMIT_BUTTON.has(attribute("theme", "primary")); //This theme makes button blue
        FrontPage.MainRow.SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        FrontPage.MainRow.PUBLIC_ACCESS_BANNER.shouldBe(visible);
        FrontPage.MainRow.PUBLIC_ACCESS_BANNER.shouldHave(text("public"));
    }

    @Test
    public void overallLinksDivExistsAndDisplayed() {
        FrontPage.OverallRow.OVERALL_DIV.shouldBe(exist);
        FrontPage.OverallRow.OVERALL_DIV.shouldBe(visible);
    }

    @Test
    public void overallLinksTextExistsAndDisplayed() {
        FrontPage.OverallRow.OVERALL_LINKS_TEXT.shouldBe(exist);
        FrontPage.OverallRow.OVERALL_LINKS_TEXT.shouldBe(visible);
        FrontPage.OverallRow.OVERALL_LINKS_TEXT.shouldHave(text("Yals already saved"));
    }

    @Test
    public void overallLinksNumberExistsAndNumber() {
        FrontPage.OverallRow.OVERALL_LINKS_NUMBER.shouldBe(exist);
        FrontPage.OverallRow.OVERALL_LINKS_NUMBER.shouldBe(visible);
        String numberText = FrontPage.OverallRow.OVERALL_LINKS_NUMBER.text();
        try {
            int numberOfSavedLinks = Integer.parseInt(numberText);
            Assert.assertTrue(numberOfSavedLinks >= 0);
        } catch (NumberFormatException e) {
            fail("Number of saved links is not a valid number");
        }
    }

}
