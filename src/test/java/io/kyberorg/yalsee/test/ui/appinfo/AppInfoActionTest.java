package io.kyberorg.yalsee.test.ui.appinfo;

import com.codeborne.selenide.Selenide;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.CookieAndYou;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.AppInfoView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject.CookieArea;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing interaction with {@link AppInfoView}.
 *
 * @since 3.5
 */
@Execution(ExecutionMode.CONCURRENT)
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
        CookieArea.LINK.click();
        assertEquals(CookieAndYou.TITLE_TEXT, Selenide.title());
    }

    /**
     * Set Value keeps after Page refresh.
     */
    @Test
    public void setValueKeepsAfterPageRefresh() {
        CookieArea.ANALYTICS_COOKIE_VALUE.click();
        open("/appInfo");
        CookieArea.ANALYTICS_COOKIE_VALUE.shouldHave(attribute("checked"));
    }
}
