package pm.axe.test.ui.appinfo;

import com.codeborne.selenide.Selenide;
import pm.axe.test.pageobjects.SettingsPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.pageobjects.external.CookieAndYou;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.appinfo.AppInfoPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.AppInfoPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.open;
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
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Link opens {@linkplain <a href="https://www.cookiesandyou.com/">Cookies And you</a>} site.
     */
    @Test
    public void linkOpensCookiesAndYouSite() {
        AppInfoPageObject.CookieSection.LINK.click();
        Assertions.assertEquals(CookieAndYou.TITLE_TEXT, Selenide.title());
    }

    /**
     * Settings Page link opens Settings Page.
     */
    @Test
    public void cookieSettingsLinkOpensSettingsPage() {
        AppInfoPageObject.CookieSection.COOKIE_SETTINGS_LINK.click();
        VaadinPageObject.waitForVaadin();
        SettingsPageObject.PAGE_ID.should(exist);
    }
}
