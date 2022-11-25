package pm.axe.test.ui.redirect;

import pm.axe.test.pageobjects.RedirectPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.redirect.RedirectPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing Visual State of {@link RedirectPage}, when accessed directly.
 *
 * @since 3.0.5
 */
public class RedirectPageDirectAccessTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/redirect-page");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that if redirect page accessed directly only banner is shown.
     */
    @Test
    public void onDirectAccessOnlyBannerShown() {
        RedirectPageObject.DIRECT_ACCESS_BANNER.shouldBe(visible);
        RedirectPageObject.REDIRECT_PAGE_CONTAINER.shouldBe(hidden);
    }

}
