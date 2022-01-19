package io.kyberorg.yalsee.test.ui.settings;

import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject;
import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.CookieSettings;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.ui.SettingsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Action Test for {@link SettingsPage}.
 *
 * @since 3.8
 */
public class SettingsPageActionTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/settings");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Set analytics cookie Value keeps after Page refresh.
     */
    @Test
    public void setAnalyticsCookieValueKeepsAfterPageRefresh() {
        CookieSettings.ANALYTICS_COOKIE_VALUE.click();
        open("/settings");
        CookieSettings.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Page Reload Button in Analytics Cookie Span reloads Page.
     */
    @Test
    public void pageReloadButtonInAnalyticsCookieSpanReloadsPage() {
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.click();
        waitForVaadin();
        SettingsPageObject.PAGE_ID.should(exist);
    }
}
