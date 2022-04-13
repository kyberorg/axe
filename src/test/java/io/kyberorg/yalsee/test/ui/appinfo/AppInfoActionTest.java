package io.kyberorg.yalsee.test.ui.appinfo;

import com.codeborne.selenide.Selenide;
import io.kyberorg.yalsee.test.pageobjects.SettingsPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.CookieAndYou;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.pages.appinfo.AppInfoPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.CookieSection;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing interaction with {@link AppInfoPage}.
 *
 * @since 3.5
 */
public class AppInfoActionTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/appInfo");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Link opens {@linkplain <a href="https://www.cookiesandyou.com/">Cookies And you</a>} site.
     */
    @Test
    public void linkOpensCookiesAndYouSite() {
        CookieSection.LINK.click();
        assertEquals(CookieAndYou.TITLE_TEXT, Selenide.title());
    }

    /**
     * Settings Page link opens Settings Page.
     */
    @Test
    public void cookieSettingsLinkOpensSettingsPage() {
        CookieSection.COOKIE_SETTINGS_LINK.click();
        waitForVaadin();
        SettingsPageObject.PAGE_ID.should(exist);
    }
}
