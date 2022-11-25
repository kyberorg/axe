package pm.axe.test.ui.redirect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.Endpoint;
import pm.axe.test.pageobjects.AppInfoPageObject;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.RedirectPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import pm.axe.ui.pages.redirect.RedirectPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing {@link RedirectPage} or its absence for internal URLs.
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
        VaadinPageObject.waitForVaadin();
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
        //will produce something like https://kyberorg.io/axe.pm
        String url = "https://kyberorg.io/" + TestUtils.getTestedEnv().getTestHost();

        storeAndOpenLink(url);
        expectRedirectPageOpened();
    }

    /**
     * External Link which has our short domain in, but not as host - should be opened with Redirect Page.
     */
    @Test
    public void onExternalShortTargetDisplayRedirectPage() {
        //will produce something like https://kyberorg.io/axe.pm
        String url = "https://kyberorg.io/" + TestUtils.getTestedEnv().getShortHost();

        storeAndOpenLink(url);
        expectRedirectPageOpened();
    }

    private void storeAndOpenLink(final String longUrl) {
        String shortLink = HomePageObject.storeAndReturnSavedUrl(longUrl);
        open(shortLink);
        VaadinPageObject.waitForVaadin();
    }

    private void expectAppInfoPageOpened() {
        AppInfoPageObject.GeneralInfoSection.SECTION.shouldBe(visible);
    }

    private void expectRedirectPageOpened() {
        RedirectPageObject.Links.ORIGIN_LINK.shouldBe(visible);
    }
}
