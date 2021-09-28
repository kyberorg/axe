package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.LoginPageObject;
import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.RegistrationPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MainViewPageObject.*;

/**
 * Testing Application View that is used for all other views.
 *
 * @since 2.8
 */
@Execution(ExecutionMode.CONCURRENT)
public class MainViewTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeTests() {
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that logo exists and visible.
     */
    @Test
    public void logoShouldExistAndVisible() {
        MainViewPageObject.LOGO.should(exist);
        MainViewPageObject.LOGO.shouldBe(visible);
    }

    /**
     * Tests that logo is rounded.
     */
    @Test
    public void logoShouldBeRounded() {
        MainViewPageObject.LOGO.shouldHave(cssValue("border-top-left-radius", "100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-top-right-radius", "100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-bottom-left-radius", "100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-bottom-right-radius", "100%"));
    }

    /**
     * Tests that logo has same background as App.
     */
    @Test
    public void logoBackgroundSameAsApp() {
        String elementColor = MainViewPageObject.LOGO.getCssValue("background-color");

        //this means that element's color is not defined and element uses app color.
        String noColor = "rgba(0, 0, 0, 0)";

        Assertions.assertEquals(elementColor, noColor);
    }

    @Test
    public void userImageShouldExistAndIsImage() {
        USER_BUTTON.should(exist);
        USER_BUTTON.shouldBe(visible);

        String userButtonTag = USER_BUTTON.getTagName();
        Assertions.assertEquals("vaadin-menu-bar", userButtonTag);
    }

    @Test
    public void userButtonOpensUserMenu() {
        USER_BUTTON.click();
        USER_MENU.should(visible);
    }

    @Test
    public void loginButtonExistsAndVisible() {
        USER_BUTTON.click();

        LOGIN_BUTTON.should(exist);
        LOGIN_BUTTON.shouldBe(visible);
    }

    @Test
    public void loginButtonOpensLoginPage() {
        USER_BUTTON.click();
        LOGIN_BUTTON.click();

        LoginPageObject.PAGE_ID.should(exist);
    }

    @Test
    public void registerButtonExistsAndVisible() {
        USER_BUTTON.click();

        REGISTER_BUTTON.should(exist);
        REGISTER_BUTTON.shouldBe(visible);
    }

    @Test
    public void registrationButtonOpensRegisterPage() {
        USER_BUTTON.click();
        REGISTER_BUTTON.click();

        RegistrationPageObject.PAGE_ID.should(exist);
    }
}
