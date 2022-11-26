package pm.axe.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import pm.axe.test.TestApp;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Common Vaadin elements and methods.
 *
 * @since 2.7.4
 */
public final class VaadinPageObject {
    public static final SelenideElement LOADING_BAR = $(".v-loading-indicator");
    public static final SelenideElement SPLASH_SCREEN = $("#splash-screen");

    private static final long VAADIN_TIMEOUT =
            Long.parseLong(System.getProperty(TestApp.Properties.VAADIN_TIMEOUT, TestApp.Defaults.VAADIN_TIMEOUT));

    /**
     * Ensures that site is loaded and Vaadin loading bar already disappear.
     */
    public static void waitForVaadin() {
        $(SPLASH_SCREEN).shouldHave(cssClass("loaded"), Duration.ofMillis(VAADIN_TIMEOUT));
        $(LOADING_BAR).shouldNotBe(visible, Duration.ofMillis(VAADIN_TIMEOUT));
        $(LOADING_BAR).shouldBe(hidden, Duration.ofMillis(VAADIN_TIMEOUT));
    }

    private VaadinPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }
}
