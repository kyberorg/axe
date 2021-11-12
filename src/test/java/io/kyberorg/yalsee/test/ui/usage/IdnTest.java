package io.kyberorg.yalsee.test.ui.usage;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.*;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.SelenideUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junitpioneer.jupiter.RetryingTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contains IDN URL multi step tests for Front page.
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public class IdnTest extends SelenideTest {

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
     * Stores russian URL.
     */
    @RetryingTest(3)
    public void russianUrl() {
        HomePageObject.storeAndOpenSavedUrl("http://кто.рф");
        // verify that KtoRF opened
        SelenideElement eggs = $(KtoRf.DIV_EGGS);
        eggs.should(exist);
    }

    /**
     * Stores finnish URL.
     */
    @RetryingTest(3)
    public void finnishUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://sää.fi");
        SelenideElement logo = $(ForecaFi.LOGO);
        logo.should(exist);
        logo.shouldHave(attribute("title", ForecaFi.LOGO_TITLE));
    }

    /**
     * Stores arabic URL.
     */
    @RetryingTest(3)
    public void arabicUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://www.101domain.com/عرب.htm");

        //needed because site loads way too long
        SelenideUtils.waitUntilSiteLoads(EXTENDED_LOAD_TIMEOUT_SECONDS);

        // verify that opens page of Registation of arabic names
        SelenideElement uniqueClassElement = $(ArabUrlRegistrar.MAIN_CLASS);
        uniqueClassElement.should(exist);
    }

    /**
     * Stores taiwanese URL.
     */
    @RetryingTest(3)
    public void taiwaneseUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://中文.tw/");

        //needed because site loads way too long
        SelenideUtils.waitUntilSiteLoads(EXTENDED_LOAD_TIMEOUT_SECONDS);

        SelenideElement navTable = $(ZhongwenTw.NAV_TABLE);
        navTable.should(exist);
    }

    /**
     * Stores German Url.
     */
    @RetryingTest(3)
    public void germanUrl() {
        HomePageObject.storeAndOpenSavedUrl("http://www.travemünde.de/");
        assertEquals(TravemundeDe.TITLE_TEXT, SelenideUtils.getPageTitle());
    }

    /**
     * Stores estonian URL.
     */
    @RetryingTest(3)
    public void estonianUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://sõnaveeb.ee");
        assertEquals(SonaveebEe.TITLE_TEXT, SelenideUtils.getPageTitle());
    }

    /**
     * Multiple languages.
     */
    @RetryingTest(3)
    public void multiLanguageUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://€.linux.it");

        // verify that opens Euro Linux Page
        SelenideElement h1 = $(EuroLinuxIt.H1);
        h1.should(exist);
        h1.shouldHave(text(EuroLinuxIt.H1_TEXT));
    }

}
