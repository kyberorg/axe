package pm.axe.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import pm.axe.ui.layouts.AxeBaseLayout;


import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Common Application elements. See {@link AxeBaseLayout}.
 *
 * @since 3.0.7
 */
public final class AxeCommonsPageObject {
    public static final SelenideElement MAIN_AREA = $(".main-area");

    private AxeCommonsPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Verifies that page has elements of {@link AxeBaseLayout}.
     */
    public static void verifyThatPageHasAxeBaseLayout() {
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
