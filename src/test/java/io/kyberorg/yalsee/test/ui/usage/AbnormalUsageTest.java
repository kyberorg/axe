package io.kyberorg.yalsee.test.ui.usage;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Simulates some not normal user activity.
 *
 * @since 1.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class AbnormalUsageTest extends SelenideTest {
    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeTest() {
        open("/");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Tests that app ignores extra attributes (?those_ones) in request.
     */
    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String extraArgument = "mineMetsa";
        final String linkToSave = "https://github.com/kyberorg/yalsee/issues/322";

        open("/?" + extraArgument);

        HomePageObject.pasteValueInFormAndSubmitIt(linkToSave);

        HomePageObject.ResultArea.RESULT_LINK.shouldNotBe(empty);
        HomePageObject.ResultArea.RESULT_LINK.shouldNotHave(text(extraArgument));
    }
}
