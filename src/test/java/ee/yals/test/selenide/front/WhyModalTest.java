package ee.yals.test.selenide.front;

import com.codeborne.selenide.WebDriverRunner;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.Endpoint.LOGIN_FORM;
import static ee.yals.test.utils.selectors.FrontSelectors.AuthRow.WHY_LINK;
import static ee.yals.test.utils.selectors.FrontSelectors.WhyModal.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests related to "Why" modal
 *
 * @since 3.0
 */
public class WhyModalTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void whyLinkShouldOpenModal() {
        clickOnWhyLink();
        WHY_MODAL.should(exist);
        WHY_MODAL.shouldBe(visible);
    }

    @Test
    public void whyModalHasTitleTextAndButtons() {
        clickOnWhyLink();
        WHY_MODAL_TITLE.should(exist).shouldBe(visible);
        WHY_MODAL_BODY.should(exist).shouldBe(visible);
        WHY_MODAL_BUTTONS.should(exist).shouldBe(visible);
        WHY_MODAL_CLOSE_BUTTON.should(exist).shouldBe(visible);
        WHY_MODAL_DEMO_BUTTON.should(exist).shouldBe(visible);
    }

    @Test
    public void whyModalHasNeededKeywords() {
        clickOnWhyLink();
        WHY_MODAL_TITLE.shouldHave(text("Why"));
        WHY_MODAL_BODY.shouldHave(text("own"));
        WHY_MODAL_CLOSE_BUTTON.shouldHave(text("close"));
        WHY_MODAL_DEMO_BUTTON.shouldHave(text("demo"));
    }

    @Test
    public void closeButtonClosesWhyModal() {
        clickOnWhyLink();
        WHY_MODAL_CLOSE_BUTTON.click();
        WHY_MODAL.shouldBe(hidden);
    }

    @Test
    public void demoButtonOpensDemoPage() {
        clickOnWhyLink();
        WHY_MODAL_DEMO_BUTTON.click();
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl.trim().contains(LOGIN_FORM));
    }

    private void clickOnWhyLink() {
        WHY_LINK.click();
    }
}
