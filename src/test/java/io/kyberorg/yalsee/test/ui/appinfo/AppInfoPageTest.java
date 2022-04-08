package io.kyberorg.yalsee.test.ui.appinfo;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.test.TestedEnv;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.CookieArea;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.PublicInfoArea.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Checking elements of public info area with information about version.
 *
 * @since 2.7
 */
public class AppInfoPageTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
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
     * Tests that Cookie Settings Span exists and visible.
     */
    @Test
    public void cookieSettingsSpanExistsAndVisible() {
        CookieArea.COOKIE_SETTING_SPAN.should(exist);
        CookieArea.COOKIE_SETTING_SPAN.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text exists and visible.
     */
    @Test
    public void cookieSettingsTextExistsAndVisible() {
        CookieArea.COOKIE_SETTINGS_TEXT.should(exist);
        CookieArea.COOKIE_SETTINGS_TEXT.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text has Words "cookie settings".
     */
    @Test
    public void cookieSettingsTextHasWordsCookieSettings() {
        CookieArea.COOKIE_SETTINGS_TEXT.shouldHave(text("cookie settings"));
    }

    /**
     * Tests that Cookie Settings Link exists and visible.
     */
    @Test
    public void cookieSettingsLinkExistsAndVisible() {
        CookieArea.COOKIE_SETTINGS_LINK.should(exist);
        CookieArea.COOKIE_SETTINGS_LINK.shouldBe(visible);
    }

    /**
     * Tests that Cookie Settings Link is active.
     */
    @Test
    public void cookieSettingsLinkIsActive() {
        CookieArea.COOKIE_SETTINGS_LINK.shouldBe(enabled);
    }

    /**
     * Tests that Cookie Settings Link should have text "Settings Page" and should lead to it.
     */
    @Test
    public void cookieSettingsLinkShouldHaveTextAndLeadsToSettingsPage() {
        CookieArea.COOKIE_SETTINGS_LINK.shouldHave(text("Settings Page"));
        CookieArea.COOKIE_SETTINGS_LINK.shouldHave(attribute("href",
                TestUtils.getTestUrl() + "/" + Endpoint.UI.SETTINGS_PAGE));
    }

    /**
     * Tests that Cookie Settings Sentence ends with dot (.).
     */
    @Test
    public void cookieSettingsSentenceEndsWithDot() {
        CookieArea.COOKIE_SETTINGS_POINT.should(exist);
        CookieArea.COOKIE_SETTINGS_POINT.shouldBe(visible);
        CookieArea.COOKIE_SETTINGS_POINT.shouldHave(text("."));
    }
}
