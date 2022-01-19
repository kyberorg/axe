package io.kyberorg.yalsee.test.ui.settings;

import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject;
import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.BetaSettings;
import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.CookieSettings;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.ui.SettingsPage;
import org.junit.jupiter.api.AfterEach;
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

    /**
     * Dark Mode Toggle enables Dark Mode.
     */
    @Test
    public void darkModeToggleEnablesDarkMode() {
        BetaSettings.DARK_MODE_VALUE.click();
        SettingsPageObject.darkModeShouldBeEnabled();
    }

    /**
     * Dark Mode Toggle returns original (light) Theme when Dark Mode enabled.
     */
    @Test
    public void darkModeToggleReturnsOriginalThemeWhenDarkModeEnabled() {
        if (SettingsPageObject.isDarkModeActive()) {
            BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
            SettingsPageObject.defaultModeShouldBeEnabled();
        } else {
            BetaSettings.DARK_MODE_VALUE.click(); //original -> dark
            BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
            SettingsPageObject.defaultModeShouldBeEnabled();
        }
    }

    /**
     * Dark Mode is applied globally and present after Page Reload.
     */
    @Test
    public void darkModeIsAppliedGloballyAndPresentAfterPageReload() {
        if (!SettingsPageObject.isDarkModeActive()) {
            BetaSettings.DARK_MODE_VALUE.click(); //original -> dark
        }
        open("/");
        waitForVaadin();
        SettingsPageObject.darkModeShouldBeEnabled();
    }

    @AfterEach
    public void cleanupAfterEachTest() {
        final boolean settingsPageOpen = SettingsPageObject.PAGE_ID.exists();
        if (!settingsPageOpen) {
            open("/settings");
            waitForVaadin();
        }
        if (SettingsPageObject.isDarkModeActive()) {
            BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
        }
    }
}
