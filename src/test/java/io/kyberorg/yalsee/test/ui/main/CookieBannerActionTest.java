package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.components.CookieBanner;
import io.kyberorg.yalsee.ui.dev.AppInfoView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing {@link CookieBanner} behavior by performing actions.
 */
public class CookieBannerActionTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        waitForVaadin();
        if (isBannerHidden()) {
            // resetting session
            open("/myLinks");
            waitForVaadin();
            if (isBannerDisplayed()) {
                //ready to test
                return;
            }
            MyLinksViewPageObject.cleanSession();
            waitForVaadin();
            if (isBannerHidden()) {
                fail("Cookie Banner didn't re-appear event after session restart");
            }
        }
    }

    @Test
    public void cookieBannerShownOncePerSession() {
        closeBannerIfAny();
        open("/");
        waitForVaadin();
        TITLE.shouldBe(hidden);
    }

    /**
     * More Info Link opens {@link AppInfoView} page.
     */
    @Test
    public void moreInfoLinkOpensAppInfoPage() {
        BannerText.LINK.click();
        AppInfoPageObject.PublicInfoArea.PUBLIC_INFO_AREA.shouldBe(visible);
    }

    /**
     * Analytics box can be checked and unchecked.
     */
    @Test
    public void analyticsBoxCanBeCheckedAndUnchecked() {
        Boxes.ANALYTICS_BOX.click();
        Boxes.ANALYTICS_BOX.shouldHave(attribute("checked"));
        Boxes.ANALYTICS_BOX.click();
        Boxes.ANALYTICS_BOX.shouldNotHave(attribute("checked"));
    }

    /**
     * Only necessary cookies button closes dialog and only technical cookies are enabled.
     */
    @Test
    public void onlyNecessaryCookiesButtonSelectsOnlyTechnicalCookies() {
        Buttons.ONLY_NECESSARY_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Only necessary cookies button enables only technical cookies, even if other were selected.
     */
    @Test
    public void onlyNecessaryCookieButtonSelectsOnlyTechnicalCookiesEvenIfOtherSelected() {
        Boxes.ANALYTICS_BOX.click();
        Buttons.ONLY_NECESSARY_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Allow Selection Button enables Selection (Single Option selected).
     */
    @Test
    public void allowSelectionButtonEnablesSelectionOne() {
        Buttons.SELECTION_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldNotHave(attribute("checked"));
    }

    /**
     * Allow Selection Button enables Selection (Both Options selected).
     */
    @Test
    public void allowSelectionButtonEnablesSelectionTwo() {
        Boxes.ANALYTICS_BOX.click();
        Buttons.SELECTION_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Allow All Button enables all options.
     */
    @Test
    public void allowAllButtonEnablesAll() {
        Boxes.ANALYTICS_BOX.click();
        Buttons.ALLOW_ALL_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }

    /**
     * Allow All Button enables all options, even when nothing was selected.
     */
    @Test
    public void allowAllButtonEnablesAllEvenWhenNoSelected() {
        Buttons.ALLOW_ALL_BUTTON.click();
        open("/appInfo");
        AppInfoPageObject.CookieArea.TECH_COOKIE_VALUE.shouldHave(attribute("checked"));
        AppInfoPageObject.CookieArea.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }
}
