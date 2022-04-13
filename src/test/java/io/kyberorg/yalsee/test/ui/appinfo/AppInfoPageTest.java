package io.kyberorg.yalsee.test.ui.appinfo;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.test.TestedEnv;
import io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.TechInfoSection;
import io.kyberorg.yalsee.test.pageobjects.YalseeCommonsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.CookieSection;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.GeneralInfoSection;
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
     * Tests if general info section is exists and visible.
     */
    @Test
    public void generalInfoSectionIsVisible() {
        GeneralInfoSection.SECTION.should(exist);
        GeneralInfoSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that general info section is wrapped by section element.
     */
    @Test
    public void generalInfoSectionIsSection() {
        GeneralInfoSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Tests if general info section has all elements that required to be.
     */
    @Test
    public void generalInfoSectionHasAllRequiredElements() {
        GeneralInfoSection.TITLE.shouldBe(visible);
        GeneralInfoSection.SPAN.shouldBe(visible);
        GeneralInfoSection.SPAN.shouldHave(text("makes"));
        GeneralInfoSection.SPAN.shouldHave(text("long links short"));
        GeneralInfoSection.SPAN.shouldHave(text("use"));
        GeneralInfoSection.SPAN.shouldHave(text("share"));
        GeneralInfoSection.SPAN.shouldHave(text("space"));
        GeneralInfoSection.SPAN.shouldHave(text("matters"));
    }

    /**
     * Tests that general info section has title with words "About" and "Application".
     */
    @Test
    public void generalInfoSectionHasWordsAboutAndApplication() {
        GeneralInfoSection.TITLE.shouldHave(text("About"));
        GeneralInfoSection.TITLE.shouldHave(text("Application"));
    }

    /**
     * Tests if Google Analytics Banner if Google Analytics enabled for tested env,
     * or absent if disabled.
     */
    @Test
    public void generalInfoSectionHasInfoAboutGoogleAnalytics() {
        TestedEnv testedEnv = TestUtils.getTestedEnv();
        if (testedEnv.isGoogleAnalyticsEnabled()) {
            GeneralInfoSection.GOOGLE_ANALYTICS_BANNER.should(exist);
            GeneralInfoSection.GOOGLE_ANALYTICS_BANNER.shouldBe(visible);
            GeneralInfoSection.GOOGLE_ANALYTICS_BANNER.shouldHave(text("Google Analytics"));
        } else {
            GeneralInfoSection.GOOGLE_ANALYTICS_BANNER.shouldNot(exist);
        }
    }

    /**
     * Cookie Area should be visible.
     */
    @Test
    public void cookieAreaShouldBeVisible() {
        CookieSection.SECTION.should(exist);
        CookieSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that Cookie Area is wrapped by section element.
     */
    @Test
    public void cookieAreaIsSection() {
        CookieSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Cookie Area should have Title.
     */
    @Test
    public void cookieAreaShouldHaveTitle() {
        CookieSection.TITLE.should(exist);
        CookieSection.TITLE.shouldBe(visible);
    }

    /**
     * Title has word "Cookies".
     */
    @Test
    public void titleHasWordCookies() {
        CookieSection.TITLE.shouldHave(text("Cookies"));
    }

    /**
     * Link should be active.
     */
    @Test
    public void linkShouldBeActive() {
        CookieSection.LINK.shouldBe(enabled);
    }

    /**
     * Link has Text "Cookies" and Href {@linkplain <a href="https://www.cookiesandyou.com/">Cookies And you</a>} site.
     */
    @Test
    public void linkShouldHaveTextCookiesAndHrefCookieAndYou() {
        CookieSection.LINK.shouldHave(text("Cookies"));
        CookieSection.LINK.shouldHave(attribute("href", "https://www.cookiesandyou.com/"));
    }


    /**
     * Tech details Span should be visible.
     */
    @Test
    public void techDetailsShouldBeVisible() {
        CookieSection.TEXT_SECTION.should(exist);
        CookieSection.TEXT_SECTION.shouldBe(visible);
    }

    /**
     * Tech details should have Words Cookies, JSESSION, Session and Google Analytics.
     */
    @Test
    public void techDetailsShouldHaveNeededWords() {
        CookieSection.TECH_DETAILS_TEXT.shouldHave(text("cookies"));
        CookieSection.TECH_DETAILS_TEXT.shouldHave(text("JSESSION"));
        CookieSection.TECH_DETAILS_TEXT.shouldHave(text("session"));
        CookieSection.TECH_DETAILS_TEXT.shouldHave(text("Google Analytics"));
    }

    /**
     * Tests that Cookie Settings Span exists and visible.
     */
    @Test
    public void cookieSettingsSpanExistsAndVisible() {
        CookieSection.COOKIE_SETTING_SPAN.should(exist);
        CookieSection.COOKIE_SETTING_SPAN.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text exists and visible.
     */
    @Test
    public void cookieSettingsTextExistsAndVisible() {
        CookieSection.COOKIE_SETTINGS_TEXT.should(exist);
        CookieSection.COOKIE_SETTINGS_TEXT.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text has Words "cookie settings".
     */
    @Test
    public void cookieSettingsTextHasWordsCookieSettings() {
        CookieSection.COOKIE_SETTINGS_TEXT.shouldHave(text("cookie settings"));
    }

    /**
     * Tests that Cookie Settings Link exists and visible.
     */
    @Test
    public void cookieSettingsLinkExistsAndVisible() {
        CookieSection.COOKIE_SETTINGS_LINK.should(exist);
        CookieSection.COOKIE_SETTINGS_LINK.shouldBe(visible);
    }

    /**
     * Tests that Cookie Settings Link is active.
     */
    @Test
    public void cookieSettingsLinkIsActive() {
        CookieSection.COOKIE_SETTINGS_LINK.shouldBe(enabled);
    }

    /**
     * Tests that Cookie Settings Link should have text "Settings Page" and should lead to it.
     */
    @Test
    public void cookieSettingsLinkShouldHaveTextAndLeadsToSettingsPage() {
        CookieSection.COOKIE_SETTINGS_LINK.shouldHave(text("Settings Page"));
        CookieSection.COOKIE_SETTINGS_LINK.shouldHave(attribute("href",
                TestUtils.getTestUrl() + "/" + Endpoint.UI.SETTINGS_PAGE));
    }

    /**
     * Tests that Cookie Settings Sentence ends with dot (.).
     */
    @Test
    public void cookieSettingsSentenceEndsWithDot() {
        CookieSection.COOKIE_SETTINGS_POINT.should(exist);
        CookieSection.COOKIE_SETTINGS_POINT.shouldBe(visible);
        CookieSection.COOKIE_SETTINGS_POINT.shouldHave(text("."));
    }

    /**
     * Tests if tech info section is exists and visible.
     */
    @Test
    public void techInfoSectionIsVisible() {
        TechInfoSection.SECTION.should(exist);
        TechInfoSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that tech info section is wrapped by section element.
     */
    @Test
    public void techInfoSectionIsSection() {
        TechInfoSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Tests if tech info section has all elements that required to be.
     */
    @Test
    public void techInfoSectionHasAllRequiredElements() {
        TechInfoSection.TITLE.shouldBe(visible);
        TechInfoSection.TITLE.shouldHave(text("Tech"));
        TechInfoSection.TITLE.shouldHave(text("Info"));

        TechInfoSection.VERSION.shouldBe(visible);
        TechInfoSection.VERSION.shouldHave(text("version"));
        TechInfoSection.VERSION.shouldHave(text("commit"));

        TechInfoSection.COMMIT_LINK.shouldBe(visible);
        TechInfoSection.COMMIT_LINK.shouldNotBe(empty);
        TechInfoSection.COMMIT_LINK.shouldHave(attribute("href"));
    }
}
