package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing Application View that is used for all other views.
 *
 * @since 2.8
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class MainViewTest extends SelenideTest {
    //emulating @BeforeAll behavior
    // this needed because tuneDriverWithCapabilities(); is not static
    private static boolean pageOpened = false;

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
        if (pageOpened) {
            return;
        }
        tuneDriverWithCapabilities();
        open("/");
        VaadinPageObject.waitForVaadin();

        pageOpened = true;
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
}
