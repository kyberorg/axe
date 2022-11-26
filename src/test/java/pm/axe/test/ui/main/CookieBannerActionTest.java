package pm.axe.test.ui.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.AppInfoPageObject;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.SettingsPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.elements.CookieBanner;
import pm.axe.ui.pages.appinfo.AppInfoPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing {@link CookieBanner} behavior by performing actions.
 */
@Disabled("Since we don't use CookieBanner anymore - no testing needed")
public class CookieBannerActionTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        VaadinPageObject.waitForVaadin();
        if (CookieBannerPageObject.isBannerHidden()) {
            // resetting session
            open("/myLinks");
            VaadinPageObject.waitForVaadin();
            if (CookieBannerPageObject.isBannerDisplayed()) {
                //ready to test
                return;
            }
            MyLinksViewPageObject.cleanSession();
            VaadinPageObject.waitForVaadin();
            if (CookieBannerPageObject.isBannerHidden()) {
                fail("Cookie Banner didn't re-appear event after session restart");
            }
        }
    }

    /**
     * Cookie Banner should be shown only once per session.
     */
    @Test
    public void cookieBannerShownOncePerSession() {
        open("/");
        VaadinPageObject.waitForVaadin();
        CookieBannerPageObject.TITLE.shouldBe(hidden);
    }

    /**
     * More Info Link opens {@link AppInfoPage} page.
     */
    @Test
    public void moreInfoLinkOpensAppInfoPage() {
        CookieBannerPageObject.BannerText.LINK.click();
        AppInfoPageObject.GeneralInfoSection.SECTION.shouldBe(visible);
    }

    /**
     * Analytics box can be checked and unchecked.
     */
    @Test
    public void analyticsBoxCanBeCheckedAndUnchecked() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.click();
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.shouldHave(attribute("checked"));
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.click();
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.shouldNotHave(attribute("checked"));
    }

    /**
     * Only necessary cookies button closes dialog and only technical cookies are enabled.
     */
    @Test
    public void onlyNecessaryCookiesButtonSelectsOnlyTechnicalCookies() {
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Only necessary cookies button enables only technical cookies, even if other were selected.
     */
    @Test
    public void onlyNecessaryCookieButtonSelectsOnlyTechnicalCookiesEvenIfOtherSelected() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.click();
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Allow Selection Button enables Selection (Single Option selected).
     */
    @Test
    public void allowSelectionButtonEnablesSelectionOne() {
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Allow Selection Button enables Selection (Both Options selected).
     */
    @Test
    public void allowSelectionButtonEnablesSelectionTwo() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.click();
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Allow All Button enables all options.
     */
    @Test
    public void allowAllButtonEnablesAll() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.click();
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Allow All Button enables all options, even when nothing was selected.
     */
    @Test
    public void allowAllButtonEnablesAllEvenWhenNoSelected() {
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.click();
        open("/settings");
        SettingsPageObject.CookieSettings.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }
}
