package io.kyberorg.yalsee.test.ui.redirect;

import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.special.RedirectPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

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
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
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
