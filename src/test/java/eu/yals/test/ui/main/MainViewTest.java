package eu.yals.test.ui.main;

import eu.yals.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.pageobjects.MainViewPageObject.LOGO;
import static eu.yals.test.pageobjects.VaadinPageObject.waitForVaadin;

public class MainViewTest extends SelenideTest {

    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        waitForVaadin();
    }

    @Test
    public void logoShouldExistAndVisible() {
        LOGO.should(exist);
        LOGO.shouldBe(visible);
    }

    @Test
    public void logoShouldBeRounded() {
        LOGO.shouldHave(cssValue("border-top-left-radius","100%"));
        LOGO.shouldHave(cssValue("border-top-right-radius","100%"));
        LOGO.shouldHave(cssValue("border-bottom-left-radius","100%"));
        LOGO.shouldHave(cssValue("border-bottom-right-radius","100%"));
    }

    @Test
    public void logoBackgroundSameAsApp() {
        String elementColor = LOGO.getCssValue("background-color");

        //this means that element's color is not defined and element uses app color.
        String noColor = "rgba(0, 0, 0, 0)";

        Assertions.assertEquals(elementColor, noColor);
    }
}
