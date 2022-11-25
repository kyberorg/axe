package pm.axe.test.ui.usage;

import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;

/**
 * Simulates some not normal user activity.
 *
 * @since 1.0
 */
public class AbnormalUsageTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that app ignores extra attributes (?those_ones) in request.
     */
    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String extraArgument = "mineMetsa";
        final String linkToSave = "https://github.com/kyberorg/axe/issues/322";

        open("/?" + extraArgument);
        VaadinPageObject.waitForVaadin();

        HomePageObject.pasteValueInFormAndSubmitIt(linkToSave);

        HomePageObject.ResultArea.RESULT_LINK.shouldNotBe(empty);
        HomePageObject.ResultArea.RESULT_LINK.shouldNotHave(text(extraArgument));
    }
}
