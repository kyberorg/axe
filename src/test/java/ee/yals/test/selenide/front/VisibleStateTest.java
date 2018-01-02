package ee.yals.test.selenide.front;


import com.codeborne.selenide.SelenideElement;
import ee.yals.test.selenide.UITest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static ee.yals.test.utils.selectors.FrontSelectors.AuthRow.*;
import static ee.yals.test.utils.selectors.FrontSelectors.ErrorRow.ERROR_MODAL;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.*;
import static ee.yals.test.utils.selectors.FrontSelectors.OverallRow.*;
import static ee.yals.test.utils.selectors.FrontSelectors.ResultRow.RESULT_DIV;
import static org.junit.Assert.fail;

/**
 * Checks state of front page (elements and so on...)
 *
 * @since 1.0
 */
public class VisibleStateTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void errorBlockIsHidden() {
        ERROR_MODAL.shouldNotBe(visible);
    }

    @Test
    public void mainBlockIsVisible() {
        MAIN_DIV.shouldBe(visible);
    }

    @Test
    public void resultBlockIsHidden() {
        RESULT_DIV.shouldNotBe(visible);
    }

    @Test
    public void formHasFieldAndButton() {
        SelenideElement formField = FORM.find(INPUT_ID);

        formField.shouldBe(exist);
        formField.shouldHave(type("text"));

        SelenideElement button = FORM.find("button");
        button.shouldBe(exist);
    }

    @Test
    public void formHasOnlyOneButton() {
        FORM.findAll("button").shouldHaveSize(1);
    }

    @Test
    public void inputAndButtonAreNotDisabled() {
        FORM.find(INPUT_ID).shouldNotBe(disabled);
        FORM.find("button").shouldNotBe(disabled);
    }

    @Test
    public void inputShouldHavePlaceholder() {
        $("form").find(INPUT_ID).shouldHave(attribute("placeholder"));
    }

    @Test
    public void shouldHaveCorrectTitle() {
        String title = title();
        Assert.assertEquals("Link shortener for friends", title);
    }

    @Test
    public void mainDivShouldHaveH2() {
        H2.shouldBe(exist);
    }

    @Test
    public void inputFieldHasLabel() {
        SelenideElement label = LONG_URL_INPUT.parent().find("label");
        label.shouldBe(exist);
        label.shouldNotBe(empty);
        label.shouldHave(attribute("for", "longUrl"));
    }

    @Test
    public void buttonIsPrimaryAndHasText() {
        SUBMIT_BUTTON.has(cssClass("btn-primary")); //This class make button blue
        SUBMIT_BUTTON.shouldHave(text("Shorten it!"));
    }

    @Test
    public void publicAccessBannerIsPresentAndHasNeededText() {
        PUBLIC_ACCESS_BANNER.shouldBe(visible);
        PUBLIC_ACCESS_BANNER.shouldHave(text("public"));

        SelenideElement form = PUBLIC_ACCESS_BANNER.closest("form");
        form.should(exist);
    }

    @Test
    public void overallLinksDivExistsAndDisplayed() {
        OVERALL_DIV.shouldBe(exist);
        OVERALL_DIV.shouldBe(visible);
    }

    @Test
    public void overallLinksTextExistsAndDisplayed() {
        OVERALL_LINKS_TEXT.shouldBe(exist);
        OVERALL_LINKS_TEXT.shouldBe(visible);
        OVERALL_LINKS_TEXT.shouldHave(text("Yals already saved"));
    }

    @Test
    public void overallLinksNumberExistsAndNumber() {
        OVERALL_LINKS_NUMBER.shouldBe(exist);
        OVERALL_LINKS_NUMBER.shouldBe(visible);
        String numberText = OVERALL_LINKS_NUMBER.text();
        try {
            int numberOfSavedLinks = Integer.parseInt(numberText);
            Assert.assertTrue(numberOfSavedLinks >= 0);
        } catch (NumberFormatException e) {
            fail("Number of saved links is not a valid number");
        }
    }

    @Test
    public void authDivShouldBeVisible() {
        AUTH_DIV.should(exist);
        AUTH_DIV.shouldBe(visible);
    }

    @Test
    public void loginButtonVisibleAndHasText() {
        LOGIN_BUTTON.should(exist);
        LOGIN_BUTTON.shouldBe(visible);
        LOGIN_BUTTON.shouldHave(text("Sign in"));
    }

    @Test
    public void loginButtonShouldBeWithinAuthDiv() {
        SelenideElement loginButtonParent = LOGIN_BUTTON.parent();
        loginButtonParent.shouldHave(id(AUTH_DIV.getAttribute("id")));
    }

    @Test
    public void myYalsLogoIsPresent() {
        MY_YALS_LOGO.should(exist);
        MY_YALS_LOGO.shouldBe(visible);
    }

    @Test
    public void myYalsLogoShouldBeWithinAuthDiv() {
        SelenideElement myYalsLogo = MY_YALS_LOGO.parent();
        myYalsLogo.shouldHave(id(AUTH_DIV.getAttribute("id")));
    }

    @Test
    public void whyLinkIsPresentAndHasTextWhy() {
        WHY_LINK.should(exist);
        WHY_LINK.shouldBe(visible);
        WHY_LINK.shouldHave(text("Why"));
    }

    @Test
    public void whyLinkShouldBeWithinAuthDiv() {
        SelenideElement whyLink = WHY_LINK.parent();
        whyLink.shouldHave(id(AUTH_DIV.getAttribute("id")));
    }
}
