package pm.axe.test.ui.appinfo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pm.axe.Endpoint;
import pm.axe.test.pageobjects.AppInfoPageObject;
import pm.axe.test.pageobjects.AxeCommonsPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

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
     * Tests if general info section is exists and visible.
     */
    @Test
    public void generalInfoSectionIsVisible() {
        AppInfoPageObject.GeneralInfoSection.SECTION.should(exist);
        AppInfoPageObject.GeneralInfoSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that general info section is wrapped by section element.
     */
    @Test
    public void generalInfoSectionIsSection() {
        AppInfoPageObject.GeneralInfoSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Tests if general info section has all elements that required to be.
     */
    @Test
    public void generalInfoSectionHasAllRequiredElements() {
        AppInfoPageObject.GeneralInfoSection.TITLE.shouldBe(visible);
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldBe(visible);
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("makes"));
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("long links short"));
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("use"));
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("share"));
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("space"));
        AppInfoPageObject.GeneralInfoSection.SPAN.shouldHave(text("matters"));
    }

    /**
     * Tests that general info section has title with words "About" and "Application".
     */
    @Test
    public void generalInfoSectionHasWordsAboutAndApplication() {
        AppInfoPageObject.GeneralInfoSection.TITLE.shouldHave(text("About"));
        AppInfoPageObject.GeneralInfoSection.TITLE.shouldHave(text("Application"));
    }


    /**
     * Cookie Area should be visible.
     */
    @Test
    public void cookieAreaShouldBeVisible() {
        AppInfoPageObject.CookieSection.SECTION.should(exist);
        AppInfoPageObject.CookieSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that Cookie Area is wrapped by section element.
     */
    @Test
    public void cookieAreaIsSection() {
        AppInfoPageObject.CookieSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Cookie Area should have Title.
     */
    @Test
    public void cookieAreaShouldHaveTitle() {
        AppInfoPageObject.CookieSection.TITLE.should(exist);
        AppInfoPageObject.CookieSection.TITLE.shouldBe(visible);
    }

    /**
     * Title has word "Cookies".
     */
    @Test
    public void titleHasWordCookies() {
        AppInfoPageObject.CookieSection.TITLE.shouldHave(text("Cookies"));
    }

    /**
     * Link should be active.
     */
    @Test
    public void linkShouldBeActive() {
        AppInfoPageObject.CookieSection.LINK.shouldBe(enabled);
    }

    /**
     * Link has Text "Cookies" and Href {@linkplain <a href="https://www.cookiesandyou.com/">Cookies And you</a>} site.
     */
    @Test
    public void linkShouldHaveTextCookiesAndHrefCookieAndYou() {
        AppInfoPageObject.CookieSection.LINK.shouldHave(text("Cookies"));
        AppInfoPageObject.CookieSection.LINK.shouldHave(attribute("href", "https://www.cookiesandyou.com/"));
    }


    /**
     * Tech details Span should be visible.
     */
    @Test
    public void techDetailsShouldBeVisible() {
        AppInfoPageObject.CookieSection.TEXT_SECTION.should(exist);
        AppInfoPageObject.CookieSection.TEXT_SECTION.shouldBe(visible);
    }

    /**
     * Tech details should have Words Cookies, JSESSION, Session and Google Analytics.
     */
    @Test
    public void techDetailsShouldHaveNeededWords() {
        AppInfoPageObject.CookieSection.TECH_DETAILS_TEXT.shouldHave(text("cookies"));
        AppInfoPageObject.CookieSection.TECH_DETAILS_TEXT.shouldHave(text("JSESSION"));
        AppInfoPageObject.CookieSection.TECH_DETAILS_TEXT.shouldHave(text("session"));
    }

    /**
     * Tests that Cookie Settings Span exists and visible.
     */
    @Test
    public void cookieSettingsSpanExistsAndVisible() {
        AppInfoPageObject.CookieSection.COOKIE_SETTING_SPAN.should(exist);
        AppInfoPageObject.CookieSection.COOKIE_SETTING_SPAN.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text exists and visible.
     */
    @Test
    public void cookieSettingsTextExistsAndVisible() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_TEXT.should(exist);
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_TEXT.shouldBe(visible);
    }

    /**
     * Test that Cookie Settings Text has Words "cookie settings".
     */
    @Test
    public void cookieSettingsTextHasWordsCookieSettings() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_TEXT.shouldHave(text("cookie settings"));
    }

    /**
     * Tests that Cookie Settings Link exists and visible.
     */
    @Test
    public void cookieSettingsLinkExistsAndVisible() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.should(exist);
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.shouldBe(visible);
    }

    /**
     * Tests that Cookie Settings Link is active.
     */
    @Test
    public void cookieSettingsLinkIsActive() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.shouldBe(enabled);
    }

    /**
     * Tests that Cookie Settings Link should have text "Settings Page" and should lead to it.
     */
    @Test
    public void cookieSettingsLinkShouldHaveTextAndLeadsToSettingsPage() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.shouldHave(text("Settings Page"));
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.shouldHave(attribute("href",
                TestUtils.getTestUrl() + "/" + Endpoint.UI.SETTINGS_PAGE));
    }

    /**
     * Tests that Cookie Settings Sentence ends with dot (.).
     */
    @Test
    public void cookieSettingsSentenceEndsWithDot() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_POINT.should(exist);
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_POINT.shouldBe(visible);
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_POINT.shouldHave(text("."));
    }

    /**
     * Tests if tech info section is exists and visible.
     */
    @Test
    public void techInfoSectionIsVisible() {
        AppInfoPageObject.TechInfoSection.SECTION.should(exist);
        AppInfoPageObject.TechInfoSection.SECTION.shouldBe(visible);
    }

    /**
     * Tests that tech info section is wrapped by section element.
     */
    @Test
    public void techInfoSectionIsSection() {
        AppInfoPageObject.TechInfoSection.SECTION.shouldHave(cssClass("section"));
    }

    /**
     * Tests if tech info section has all elements that required to be.
     */
    @Test
    public void techInfoSectionHasAllRequiredElements() {
        AppInfoPageObject.TechInfoSection.TITLE.shouldBe(visible);
        AppInfoPageObject.TechInfoSection.TITLE.shouldHave(text("Tech"));
        AppInfoPageObject.TechInfoSection.TITLE.shouldHave(text("Info"));

        AppInfoPageObject.TechInfoSection.VERSION.shouldBe(visible);
        AppInfoPageObject.TechInfoSection.VERSION.shouldHave(text("version"));
        AppInfoPageObject.TechInfoSection.VERSION.shouldHave(text("commit"));

        AppInfoPageObject.TechInfoSection.COMMIT_LINK.shouldBe(visible);
        AppInfoPageObject.TechInfoSection.COMMIT_LINK.shouldNotBe(empty);
        AppInfoPageObject.TechInfoSection.COMMIT_LINK.shouldHave(attribute("href"));
    }
}
