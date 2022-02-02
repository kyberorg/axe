package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.SelenideUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing Application View that is used for all other views.
 *
 * @since 2.8
 */
public class MainViewTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/");
        VaadinPageObject.waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
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

    /**
     * Long Version of Logo is used at Menu.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/748")
    public void longVersionOfLogoIsUsedAtMenu() {
        SelenideUtils.assertThatImageIsNotSquared(MainViewPageObject.LOGO);
    }

    /**
     * Click on Logo opens Home Page.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/604")
    public void clickOnLogoOpensHomePage() {
        MainViewPageObject.LOGO.click();
        checkThatHomePageOpened();
    }

    private void checkThatHomePageOpened() {
        HomePageObject.MainArea.TITLE.should(exist);
        HomePageObject.MainArea.TITLE.shouldBe(visible);
    }
}
