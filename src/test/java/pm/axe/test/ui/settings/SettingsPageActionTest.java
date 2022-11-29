package pm.axe.test.ui.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.SettingsPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.settings.SettingsPage;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.open;

/**
 * Action Test for {@link SettingsPage}.
 *
 * @since 3.8
 */
public class SettingsPageActionTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/settings");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Set analytics cookie Value keeps after Page refresh.
     */
    @Test
    public void setAnalyticsCookieValueKeepsAfterPageRefresh() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.click();
        open("/settings");
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Dark Mode Toggle enables Dark Mode.
     */
    @Test
    public void darkModeToggleEnablesDarkMode() {
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click();
        SettingsPageObject.darkModeShouldBeEnabled();
    }

    /**
     * Dark Mode Toggle returns original (light) Theme when Dark Mode enabled.
     */
    @Test
    public void darkModeToggleReturnsOriginalThemeWhenDarkModeEnabled() {
        if (SettingsPageObject.isDarkModeActive()) {
            SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
            SettingsPageObject.defaultModeShouldBeEnabled();
        } else {
            SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click(); //original -> dark
            SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
            SettingsPageObject.defaultModeShouldBeEnabled();
        }
    }

    /**
     * Dark Mode is applied globally and present after Page Reload.
     */
    @Test
    public void darkModeIsAppliedGloballyAndPresentAfterPageReload() {
        if (!SettingsPageObject.isDarkModeActive()) {
            SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click(); //original -> dark
        }
        open("/");
        VaadinPageObject.waitForVaadin();
        SettingsPageObject.darkModeShouldBeEnabled();
    }

    /**
     * Cleanup: resets site theme to original.
     */
    @AfterEach
    public void cleanupAfterEachTest() {
        final boolean settingsPageOpen = SettingsPageObject.PAGE_ID.exists();
        if (!settingsPageOpen) {
            open("/settings");
            VaadinPageObject.waitForVaadin();
        }
        if (SettingsPageObject.isDarkModeActive()) {
            SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click(); //dark -> original
        }
    }
}
