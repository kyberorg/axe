package io.kyberorg.yalsee.test.ui.redirect;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.pageobjects.AppInfoPageObject;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.special.RedirectView;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Testing {@link RedirectView} or its absence for internal URLs.
 *
 * @since 3.7
 */
@Slf4j
public class RedirectPageForInternalsTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Internal Link to AppInfo Page should open without RedirectView.
     */
    @Test
    public void onInternalTargetNoRedirectPage() {
        String url = TestUtils.getTestUrl() + "/" + Endpoint.UI.APP_INFO_PAGE;
        storeAndOpenLink(url);
        expectAppInfoPageOpened();
    }

    /**
     * Internal Link with short Domain to AppInfo Page should open without RedirectView.
     */
    @Test
    public void onInternalShortTargetNoRedirectPage() {
        String url = TestUtils.getAppShortUrl() + "/" + Endpoint.UI.APP_INFO_PAGE;
        storeAndOpenLink(url);
        expectAppInfoPageOpened();
    }

    /**
     * External Link which has our domain in, but not as host - should be opened with Redirect Page.
     */
    @Test
    public void onExternalTargetDisplayRedirectPage() {
        //will produce something like https://kyberorg.io/yals.ee
        String url = "https://kyberorg.io/" + TestUtils.getTestedEnv().getTestHost();

        storeAndOpenLink(url);
        expectRedirectPageOpened();
    }

    /**
     * External Link which has our short domain in, but not as host - should be opened with Redirect Page
     */
    @Test
    public void onExternalShortTargetDisplayRedirectPage() {
        //will produce something like https://kyberorg.io/yls.ee
        String url = "https://kyberorg.io/" + TestUtils.getTestedEnv().getShortHost();

        storeAndOpenLink(url);
        expectRedirectPageOpened();
    }

    private void storeAndOpenLink(final String longUrl) {
        String shortLink = HomePageObject.storeAndReturnSavedUrl(longUrl);
        open(shortLink);
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    private void expectAppInfoPageOpened() {
        AppInfoPageObject.PublicInfoArea.PUBLIC_INFO_AREA.shouldBe(visible);
    }

    private void expectRedirectPageOpened() {
        RedirectPageObject.REDIRECT_PAGE_CONTAINER.shouldBe(visible);
    }
}
