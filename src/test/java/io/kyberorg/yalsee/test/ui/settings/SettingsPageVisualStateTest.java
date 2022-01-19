package io.kyberorg.yalsee.test.ui.settings;

import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject.CookieSettings;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.SettingsPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Testing visual state of {@link SettingsPage}.
 *
 * @since 3.8
 */
public class SettingsPageVisualStateTest extends SelenideTest {

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

}
