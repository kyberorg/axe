package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Common Application elements. See {@link YalseeLayout}.
 *
 * @since 3.0.7
 */
public class YalseeCommonsPageObject {
    public static final SelenideElement MAIN_AREA = $(".main-area");

    /**
     * Verifies that page has elements of {@link YalseeLayout}.
     */
    public static void verifyThatPageHasYalseeLayout() {
        verifyPageHasMainArea();
        verifyThatMainAreaHasBorder();
    }

    private static void verifyPageHasMainArea() {
        MAIN_AREA.shouldBe(visible);
    }

    private static void verifyThatMainAreaHasBorder() {
        MAIN_AREA.shouldHave(cssClass("border"));
    }
}
