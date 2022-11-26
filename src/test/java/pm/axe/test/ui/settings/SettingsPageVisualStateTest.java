package pm.axe.test.ui.settings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.AxeCommonsPageObject;
import pm.axe.test.pageobjects.SettingsPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.settings.SettingsPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing visual state of {@link SettingsPage}.
 *
 * @since 3.8
 */
public class SettingsPageVisualStateTest extends SelenideTest {

    /**
     * Actions before all tests started.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/settings");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that Page has common application Layout.
     */
    @Test
    public void pageHasCommonLayout() {
        AxeCommonsPageObject.verifyThatPageHasAxeBaseLayout();
    }

    /**
     * Page Title should exist and visible.
     */
    @Test
    public void pageTitleShouldExistAndBeVisible() {
        SettingsPageObject.PAGE_TITLE.should(exist);
        SettingsPageObject.PAGE_TITLE.shouldBe(visible);
    }

    /**
     * Page Title should have text "Application Settings".
     */
    @Test
    public void pageTitleShouldHaveTextApplicationSettings() {
        SettingsPageObject.PAGE_ID.shouldHave(text("Application Settings"));
    }

    /**
     * Subtitle exists and its tag is H5.
     */
    @Test
    public void subtitleExists() {
        SettingsPageObject.CookieSettings.TITLE.should(exist);
        SettingsPageObject.CookieSettings.TITLE.shouldBe(visible);
    }

    /**
     * Subtitle has Words "Cookie Settings".
     */
    @Test
    public void subtitleShouldHaveWordsCurrentSettings() {
        SettingsPageObject.CookieSettings.TITLE.shouldHave(text("Cookie Settings"));
    }

    /**
     * Tech Cookie Span exists and visible.
     */
    @Test
    public void techCookieSpanExistsAndVisible() {
        SettingsPageObject.CookieSettings.TECH_COOKIE_SPAN.should(exist);
        SettingsPageObject.CookieSettings.TECH_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Tech cookies label exists and visible.
     */
    @Test
    public void techCookiesLabelExistsAndVisible() {
        SettingsPageObject.CookieSettings.TECH_COOKIE_LABEL.should(exist);
        SettingsPageObject.CookieSettings.TECH_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Tech Cookies Label has Words "Technical cookies".
     */
    @Test
    public void techCookiesLabelHasWordsTechnicalCookies() {
        SettingsPageObject.CookieSettings.TECH_COOKIE_LABEL.shouldHave(text("Technical cookies"));
    }

    /**
     * Tech Cookies Value exists and visible.
     */
    @Test
    public void techCookiesValueExistsAndVisible() {
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.should(exist);
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Tech Cookies Value disabled and not clickable.
     */
    @Test
    public void techCookiesValueDisabledAndNotClickable() {
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("disabled"));
    }

    /**
     * Analytics Cookie Span exists and visible.
     */
    @Test
    public void analyticsCookieSpanExistsAndVisible() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_SPAN.should(exist);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label exists and visible.
     */
    @Test
    public void analyticsCookiesLabelExistsAndVisible() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_LABEL.should(exist);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label has Words "Analytics cookies".
     */
    @Test
    public void analyticsCookiesLabelHasWordsAnalyticsCookies() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_LABEL.shouldHave(text("Analytics cookies"));
    }

    /**
     * Analytics Cookies Value exists and visible.
     */
    @Test
    public void analyticsCookiesValueExistsAndVisible() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.should(exist);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Analytics Cookies Value enabled and clickable.
     */
    @Test
    public void analyticsCookiesValueEnabledAndClickable() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldBe(enabled);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.click();
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.click();
    }

    /**
     * Analytics Cookie Span has Page Reload Postfix inside.
     */
    @Test
    public void analyticsCookieSpanHasPageReloadPostfixInside() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_POSTFIX_SPAN.should(exist);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_POSTFIX_SPAN.shouldBe(visible);
    }

    /**
     * Analytics Cookie Page Reload Postfix has Button, and it has text "Page Reload".
     */
    @Test
    public void analyticsCookiePageReloadPostfixHasPageReloadButton() {
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.should(exist);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.shouldBe(visible);
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.shouldHave(text("Page Reload"));
    }

    /**
     * Beta Settings Title exist and visible.
     */
    @Test
    public void betaSettingsTitleExistAndVisible() {
        SettingsPageObject.BetaSettings.TITLE.should(exist);
        SettingsPageObject.BetaSettings.TITLE.shouldBe(visible);
    }

    /**
     * Beta Settings Title has Words "Beta" and "Feature preview".
     */
    @Test
    public void betaSettingsTitleHasNeededWords() {
        SettingsPageObject.BetaSettings.TITLE.shouldHave(text("Beta"));
        SettingsPageObject.BetaSettings.TITLE.shouldHave(text("Feature preview"));
    }

    /**
     * Dark Mode Span exist and visible.
     */
    @Test
    public void darkModeSpanExistAndVisible() {
        SettingsPageObject.BetaSettings.DARK_MODE_SPAN.should(exist);
        SettingsPageObject.BetaSettings.DARK_MODE_SPAN.shouldBe(visible);
    }

    /**
     * Dark Mode Label exist and visible.
     */
    @Test
    public void darkModeLabelExistAndVisible() {
        SettingsPageObject.BetaSettings.DARK_MODE_LABEL.should(exist);
        SettingsPageObject.BetaSettings.DARK_MODE_LABEL.shouldBe(visible);
    }

    /**
     * Dark Mode Label has Words "Dark Mode".
     */
    @Test
    public void darkModeLabelHasWordsDarkMode() {
        SettingsPageObject.BetaSettings.DARK_MODE_LABEL.shouldHave(text("Dark Mode"));
    }

    /**
     * Dark Mode Value exist and visible.
     */
    @Test
    public void darkModeValueExistAndVisible() {
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.should(exist);
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.shouldBe(visible);
    }

    /**
     * Dark Mode Value enabled and clickable.
     */
    @Test
    public void darkModeValueEnabledAndClickable() {
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.shouldBe(enabled);
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click();
        SettingsPageObject.BetaSettings.DARK_MODE_VALUE.click();
    }

}
