package io.kyberorg.yalsee.test.ui.settings;

import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.BetaSettings;
import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.CookieSettings;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.SettingsPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.PAGE_ID;
import static io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.PAGE_TITLE;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

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
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Tests that Page has common application Layout.
     */
    @Test
    public void pageHasCommonLayout() {
        YalseeCommonsPageObject.verifyThatPageHasYalseeLayout();
    }

    /**
     * Page Title should exist and visible.
     */
    @Test
    public void pageTitleShouldExistAndBeVisible() {
        PAGE_TITLE.should(exist);
        PAGE_TITLE.shouldBe(visible);
    }

    /**
     * Page Title should have text "Application Settings".
     */
    @Test
    public void pageTitleShouldHaveTextApplicationSettings() {
        PAGE_ID.shouldHave(text("Application Settings"));
    }

    /**
     * Subtitle exists and its tag is H5.
     */
    @Test
    public void subtitleExists() {
        CookieSettings.TITLE.should(exist);
        CookieSettings.TITLE.shouldBe(visible);
    }

    /**
     * Subtitle has Words "Cookie Settings".
     */
    @Test
    public void subtitleShouldHaveWordsCurrentSettings() {
        CookieSettings.TITLE.shouldHave(text("Cookie Settings"));
    }

    /**
     * Tech Cookie Span exists and visible.
     */
    @Test
    public void techCookieSpanExistsAndVisible() {
        CookieSettings.TECH_COOKIE_SPAN.should(exist);
        CookieSettings.TECH_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Tech cookies label exists and visible.
     */
    @Test
    public void techCookiesLabelExistsAndVisible() {
        CookieSettings.TECH_COOKIE_LABEL.should(exist);
        CookieSettings.TECH_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Tech Cookies Label has Words "Technical cookies".
     */
    @Test
    public void techCookiesLabelHasWordsTechnicalCookies() {
        CookieSettings.TECH_COOKIE_LABEL.shouldHave(text("Technical cookies"));
    }

    /**
     * Tech Cookies Value exists and visible.
     */
    @Test
    public void techCookiesValueExistsAndVisible() {
        CookieSettings.TECH_COOKIE_VALUE.should(exist);
        CookieSettings.TECH_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Tech Cookies Value disabled and not clickable.
     */
    @Test
    public void techCookiesValueDisabledAndNotClickable() {
        CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("disabled"));
    }

    /**
     * Analytics Cookie Span exists and visible.
     */
    @Test
    public void analyticsCookieSpanExistsAndVisible() {
        CookieSettings.ANALYTICS_COOKIE_SPAN.should(exist);
        CookieSettings.ANALYTICS_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label exists and visible.
     */
    @Test
    public void analyticsCookiesLabelExistsAndVisible() {
        CookieSettings.ANALYTICS_COOKIE_LABEL.should(exist);
        CookieSettings.ANALYTICS_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label has Words "Analytics cookies".
     */
    @Test
    public void analyticsCookiesLabelHasWordsAnalyticsCookies() {
        CookieSettings.ANALYTICS_COOKIE_LABEL.shouldHave(text("Analytics cookies"));
    }

    /**
     * Analytics Cookies Value exists and visible.
     */
    @Test
    public void analyticsCookiesValueExistsAndVisible() {
        CookieSettings.ANALYTICS_COOKIE_VALUE.should(exist);
        CookieSettings.ANALYTICS_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Analytics Cookies Value enabled and clickable.
     */
    @Test
    public void analyticsCookiesValueEnabledAndClickable() {
        CookieSettings.ANALYTICS_COOKIE_VALUE.shouldBe(enabled);
        CookieSettings.ANALYTICS_COOKIE_VALUE.click();
        CookieSettings.ANALYTICS_COOKIE_VALUE.click();
    }

    /**
     * Analytics Cookie Span has Page Reload Postfix inside.
     */
    @Test
    public void analyticsCookieSpanHasPageReloadPostfixInside() {
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_SPAN.should(exist);
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_SPAN.shouldBe(visible);
    }

    /**
     * Analytics Cookie Page Reload Postfix has Button, and it has text "Page Reload".
     */
    @Test
    public void analyticsCookiePageReloadPostfixHasPageReloadButton() {
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.should(exist);
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.shouldBe(visible);
        CookieSettings.ANALYTICS_COOKIE_POSTFIX_BUTTON.shouldHave(text("Page Reload"));
    }

    /**
     * Beta Settings Title exist and visible.
     */
    @Test
    public void betaSettingsTitleExistAndVisible() {
        BetaSettings.TITLE.should(exist);
        BetaSettings.TITLE.shouldBe(visible);
    }

    /**
     * Beta Settings Title has Words "Beta" and "Feature preview".
     */
    @Test
    public void betaSettingsTitleHasNeededWords() {
        BetaSettings.TITLE.shouldHave(text("Beta"));
        BetaSettings.TITLE.shouldHave(text("Feature preview"));
    }

    /**
     * Dark Mode Span exist and visible.
     */
    @Test
    public void darkModeSpanExistAndVisible() {
        BetaSettings.DARK_MODE_SPAN.should(exist);
        BetaSettings.DARK_MODE_SPAN.shouldBe(visible);
    }

    /**
     * Dark Mode Label exist and visible.
     */
    @Test
    public void darkModeLabelExistAndVisible() {
        BetaSettings.DARK_MODE_LABEL.should(exist);
        BetaSettings.DARK_MODE_LABEL.shouldBe(visible);
    }

    /**
     * Dark Mode Label has Words "Dark Mode".
     */
    @Test
    public void darkModeLabelHasWordsDarkMode() {
        BetaSettings.DARK_MODE_LABEL.shouldHave(text("Dark Mode"));
    }

    /**
     * Dark Mode Value exist and visible.
     */
    @Test
    public void darkModeValueExistAndVisible() {
        BetaSettings.DARK_MODE_VALUE.should(exist);
        BetaSettings.DARK_MODE_VALUE.shouldBe(visible);
    }

    /**
     * Dark Mode Value enabled and clickable.
     */
    @Test
    public void darkModeValueEnabledAndClickable() {
        BetaSettings.DARK_MODE_VALUE.shouldBe(enabled);
        BetaSettings.DARK_MODE_VALUE.click();
        BetaSettings.DARK_MODE_VALUE.click();
    }

}
