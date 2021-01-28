package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

public class MainViewTest extends SelenideTest {

    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    @Test
    public void logoShouldExistAndVisible() {
        MainViewPageObject.LOGO.should(exist);
        MainViewPageObject.LOGO.shouldBe(visible);
    }

    @Test
    public void logoShouldBeRounded() {
        MainViewPageObject.LOGO.shouldHave(cssValue("border-top-left-radius","100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-top-right-radius","100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-bottom-left-radius","100%"));
        MainViewPageObject.LOGO.shouldHave(cssValue("border-bottom-right-radius","100%"));
    }

    @Test
    public void logoBackgroundSameAsApp() {
        String elementColor = MainViewPageObject.LOGO.getCssValue("background-color");

        //this means that element's color is not defined and element uses app color.
        String noColor = "rgba(0, 0, 0, 0)";

        Assertions.assertEquals(elementColor, noColor);
    }
}
