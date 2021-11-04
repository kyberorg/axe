package io.kyberorg.yalsee.test.ui.appinfo;

import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.TestedEnv;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.CookieArea;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.PublicInfoArea.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checking elements of public info area with information about version.
 *
 * @since 2.7
 */
@Execution(ExecutionMode.CONCURRENT)
public class AppInfoPageTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeAll
    public static void beforeTests() {
        open("/appInfo");
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
     * Tests if public area is exists and visible.
     */
    @Test
    public void publicAreaIsVisible() {
        PUBLIC_INFO_AREA.should(exist);
        PUBLIC_INFO_AREA.shouldBe(visible);
    }

    /**
     * Tests if public area has all elements that required to be.
     */
    @Test
    public void publicAreaHasAllRequiredElements() {
        VERSION.shouldBe(visible);
        VERSION.shouldHave(text("version"));
        VERSION.shouldHave(text("commit"));

        COMMIT_LINK.shouldBe(visible);
        COMMIT_LINK.shouldNotBe(empty);
        COMMIT_LINK.shouldHave(attribute("href"));
    }

    /**
     * Tests if Google Analytics Banner if Google Analytics enabled for tested env,
     * or absent if disabled.
     */
    @Test
    public void publicAreaHasInfoAboutGoogleAnalytics() {
        TestedEnv testedEnv = TestUtils.getTestedEnv();
        if (testedEnv.isGoogleAnalyticsEnabled()) {
            GOOGLE_ANALYTICS_BANNER.should(exist);
            GOOGLE_ANALYTICS_BANNER.shouldBe(visible);
            GOOGLE_ANALYTICS_BANNER.shouldHave(text("Google Analytics"));
        } else {
            GOOGLE_ANALYTICS_BANNER.shouldNot(exist);
        }
    }

    /**
     * Cookie Area should be visible.
     */
    @Test
    public void cookieAreaShouldBeVisible() {
        CookieArea.COOKIE_AREA.should(exist);
        CookieArea.COOKIE_AREA.shouldBe(visible);
    }

    /**
     * Cookie Area should have Title.
     */
    @Test
    public void cookieAreaShouldHaveTitle() {
        CookieArea.TITLE.should(exist);
        CookieArea.TITLE.shouldBe(visible);
    }

    /**
     * Title has word "Cookies".
     */
    @Test
    public void titleHasWordCookies() {
        CookieArea.TITLE.shouldHave(text("Cookies"));
    }

    /**
     * Link should be active.
     */
    @Test
    public void linkShouldBeActive() {
        CookieArea.LINK.shouldBe(enabled);
    }

    /**
     * Link has Text "Cookies" and Href {@linkplain <a href="https://www.cookiesandyou.com/">Cookies And you</a>} site.
     */
    @Test
    public void linkShouldHaveTextCookiesAndHrefCookieAndYou() {
        CookieArea.LINK.shouldHave(text("Cookies"));
        CookieArea.LINK.shouldHave(attribute("href", "https://www.cookiesandyou.com/"));
    }


    /**
     * Tech details Span should be visible.
     */
    @Test
    public void techDetailsShouldBeVisible() {
        CookieArea.TEXT_SECTION.should(exist);
        CookieArea.TEXT_SECTION.shouldBe(visible);
    }

    /**
     * Tech details should have Words Cookies, JSESSION, Session and Google Analytics.
     */
    @Test
    public void techDetailsShouldHaveNeededWords() {
        CookieArea.TECH_DETAILS_TEXT.shouldHave(text("cookies"));
        CookieArea.TECH_DETAILS_TEXT.shouldHave(text("JSESSION"));
        CookieArea.TECH_DETAILS_TEXT.shouldHave(text("session"));
        CookieArea.TECH_DETAILS_TEXT.shouldHave(text("Google Analytics"));
    }

    /**
     * Subtitle exists and its tag is H5.
     */
    @Test
    public void subtitleExistsAndH5() {
        CookieArea.CURRENT_SETTING_TITLE.should(exist);
        CookieArea.CURRENT_SETTING_TITLE.shouldBe(visible);
        String subtitleTag = CookieArea.CURRENT_SETTING_TITLE.getTagName();
        assertEquals("h5", subtitleTag);
    }

    /**
     * Subtitle has Words "Current Settings".
     */
    @Test
    public void subtitleShouldHaveWordsCurrentSettings() {
        CookieArea.CURRENT_SETTING_TITLE.shouldHave(text("Current Settings"));
    }

    /**
     * Tech Cookie Span exists and visible.
     */
    @Test
    public void techCookieSpanExistsAndVisible() {
        CookieArea.TECH_COOKIE_SPAN.should(exist);
        CookieArea.TECH_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Tech cookies label exists and visible.
     */
    @Test
    public void techCookiesLabelExistsAndVisible() {
        CookieArea.TECH_COOKIE_LABEL.should(exist);
        CookieArea.TECH_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Tech Cookies Label has Words "Technical Cookies".
     */
    @Test
    public void techCookiesLabelHasWordsTechnicalCookies() {
        CookieArea.TECH_COOKIE_LABEL.shouldHave(text("Technical Cookies"));
    }

    /**
     * Tech Cookies Value exists and visible.
     */
    @Test
    public void techCookiesValueExistsAndVisible() {
        CookieArea.TECH_COOKIE_VALUE.should(exist);
        CookieArea.TECH_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Tech Cookies Value disabled and not clickable.
     */
    @Test
    public void TechCookiesValueDisabledAndNotClickable() {
        CookieArea.TECH_COOKIE_VALUE.shouldBe(disabled);
    }

    /**
     * Analytics Cookie Span exists and visible.
     */
    @Test
    public void analyticsCookieSpanExistsAndVisible() {
        CookieArea.ANALYTICS_COOKIE_SPAN.should(exist);
        CookieArea.ANALYTICS_COOKIE_SPAN.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label exists and visible.
     */
    @Test
    public void analyticsCookiesLabelExistsAndVisible() {
        CookieArea.ANALYTICS_COOKIE_LABEL.should(exist);
        CookieArea.ANALYTICS_COOKIE_LABEL.shouldBe(visible);
    }

    /**
     * Analytics Cookies Label has Words "Analytics Cookies".
     */
    @Test
    public void analyticsCookiesLabelHasWordsAnalyticsCookies() {
        CookieArea.ANALYTICS_COOKIE_LABEL.shouldHave(text("Analytics Cookies"));
    }

    /**
     * Analytics Cookies Value exists and visible.
     */
    @Test
    public void analyticsCookiesValueExistsAndVisible() {
        CookieArea.ANALYTICS_COOKIE_VALUE.should(exist);
        CookieArea.ANALYTICS_COOKIE_VALUE.shouldBe(visible);
    }

    /**
     * Analytics Cookies Value enabled and clickable.
     */
    @Test
    public void analyticsCookiesValueEnabledAndClickable() {
        CookieArea.ANALYTICS_COOKIE_VALUE.shouldBe(enabled);
        CookieArea.ANALYTICS_COOKIE_VALUE.click();
        CookieArea.ANALYTICS_COOKIE_VALUE.click();
    }
}
