package pm.axe.test.ui.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.MainViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.SelenideUtils;

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
    @Issue("https://github.com/kyberorg/axe/issues/748")
    public void longVersionOfLogoIsUsedAtMenu() {
        SelenideUtils.assertThatImageIsNotSquared(MainViewPageObject.LOGO);
    }

    /**
     * Click on Logo opens Home Page.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/604")
    public void clickOnLogoOpensHomePage() {
        MainViewPageObject.LOGO.click();
        checkThatHomePageOpened();
    }

    private void checkThatHomePageOpened() {
        HomePageObject.MainArea.TITLE.should(exist);
        HomePageObject.MainArea.TITLE.shouldBe(visible);
    }
}
