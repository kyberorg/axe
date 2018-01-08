package ee.yals.test.selenide.loginPage;

import ee.yals.Endpoint;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.selectors.LoginPageSelectors.Form.*;

/**
 * Checks state of login page (elements and so on...)
 *
 * @since 3.0
 */
public class VisualStateTest extends UITest {

    @Before
    public void openUrl() {
        open(Endpoint.LOGIN_FORM);
    }

    @Test
    public void formTitleExistsAndHasNeededText() {
        HEADING.should(exist);
        HEADING.shouldBe(visible);
        HEADING.shouldHave(text("Please")).shouldHave(text("log in"));
    }

    @Test
    public void formSubTitleExistsAndHasNeededText() {
        SUB_HEADING.should(exist);
        SUB_HEADING.shouldBe(visible);
        SUB_HEADING.shouldHave(text("to continue"));
    }

    @Test
    public void userInputShouldBeVisibleAndHavePlaceholder() {
        USER_INPUT.should(exist).shouldBe(visible);
        USER_INPUT.shouldBe(empty);
        USER_INPUT.shouldHave(attribute("placeholder"));
    }

    @Test
    public void passwordInputShouldBeVisibleHavePlaceholderAndTypePassword() {
        PASSWORD_INPUT.should(exist).shouldBe(visible);
        PASSWORD_INPUT.shouldBe(empty);
        PASSWORD_INPUT.shouldHave(attribute("placeholder"));
        PASSWORD_INPUT.shouldHave(type("password"));
    }

    @Test
    public void demoStringExistsNotEmptyAndHasTextDemo() {
        DEMO_STRING.should(exist).shouldBe(visible);
        DEMO_STRING.shouldNotBe(empty);
        DEMO_STRING.shouldHave(text("demo"));
    }

    @Test
    public void loginButtonExistsHaveTextAndActive() {
        LOGIN_BUTTON.should(exist).shouldBe(visible);
        LOGIN_BUTTON.shouldBe(enabled);
        LOGIN_BUTTON.shouldHave(text("Log in"));
        LOGIN_BUTTON.shouldHave(cssClass("btn-primary"));
        LOGIN_BUTTON.shouldHave(cssClass("btn-lg"));
    }
}
