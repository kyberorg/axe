package eu.yals.test.selenide.front;


import com.codeborne.selenide.SelenideElement;
import eu.yals.test.selenide.UITest;
import eu.yals.test.utils.pages.FrontPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
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
        FrontPage.MainRow.MAIN_DIV.shouldBe(visible);
    }

    @Test
    public void resultBlockIsHidden() {
        FrontPage.ResultRow.RESULT_DIV.shouldNotBe(visible);
    }

    @Test
    public void formHasFieldAndButton() {
        SelenideElement formField = FrontPage.MainRow.FORM.find(FrontPage.MainRow.INPUT_ID);

        formField.shouldBe(exist);
        formField.shouldHave(type("text"));

        SelenideElement button = FrontPage.MainRow.FORM.find("button");
        button.shouldBe(exist);
    }

    @Test
    public void formHasOnlyOneButton() {
        FrontPage.MainRow.FORM.findAll("button").shouldHaveSize(1);
    }

    @Test
    public void inputAndButtonAreNotDisabled() {
        FrontPage.MainRow.FORM.find(FrontPage.MainRow.INPUT_ID).shouldNotBe(disabled);
        FrontPage.MainRow.FORM.find("button").shouldNotBe(disabled);
    }

    @Test
    public void inputShouldHavePlaceholder() {
        $("form").find(FrontPage.MainRow.INPUT_ID).shouldHave(attribute("placeholder"));
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
        FrontPage.MainRow.SUBMIT_BUTTON.has(cssClass("btn-primary")); //This class make button blue
        FrontPage.MainRow.SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        FrontPage.MainRow.PUBLIC_ACCESS_BANNER.shouldBe(visible);
        FrontPage.MainRow.PUBLIC_ACCESS_BANNER.shouldHave(text("public"));

        SelenideElement form = FrontPage.MainRow.PUBLIC_ACCESS_BANNER.closest("form");
        form.should(exist);
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
