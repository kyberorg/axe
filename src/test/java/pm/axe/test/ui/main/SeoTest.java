package pm.axe.test.ui.main;

import pm.axe.test.TestedEnv;
import pm.axe.test.pageobjects.SettingsPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.MainViewPageObject;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tests SEO (Search Engine Optimization) related stuff.
 *
 * @since 3.0.2
 */
public class SeoTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/");
        VaadinPageObject.waitForVaadin();
        // we have to enable analytics first
        if (CookieBannerPageObject.isBannerDisplayed()) {
            CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.click();
        } else {
            //if banner is gone - more complex logic needed to enable analytics.
            open("/settings");
            VaadinPageObject.waitForVaadin();
            SettingsPageObject.CookieSettings.ANALYTICS_COOKIE_VALUE.click();
        }
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Checks if we have meta needed meta.
     */
    @Test
    public void hasNeededMetaTags() {
        $("meta[name='title']").should(exist);
        $("meta[name='description']").should(exist);
        $("meta[name='og:type']").should(exist);
        $("meta[name='og:url']").should(exist);
        $("meta[name='og:title']").should(exist);
        $("meta[name='og:image']").should(exist);
        $("meta[name='twitter:card']").should(exist);
        $("meta[name='twitter:url']").should(exist);
        $("meta[name='twitter:title']").should(exist);
        $("meta[name='twitter:description']").should(exist);
        $("meta[name='twitter:image']").should(exist);

    }
}
