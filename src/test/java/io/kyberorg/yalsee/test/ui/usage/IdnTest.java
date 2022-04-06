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
    @RetryingTest(2)
    public void russianUrl() {
        HomePageObject.storeAndOpenSavedUrl("http://кто.рф");
        // verify that KtoRF opened
        SelenideElement regDiv = $(KtoRf.DIV_REG);
        regDiv.should(exist);
    }

    /**
     * Stores finnish URL.
     */
    @RetryingTest(2)
    public void finnishUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://sää.fi");
        SelenideElement logo = $(ForecaFi.LOGO);
        logo.should(exist);
        logo.shouldHave(attribute("title", ForecaFi.LOGO_TITLE));
    }

    /**
     * Stores arabic URL.
     */
    @RetryingTest(2)
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
    @RetryingTest(2)
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
    @RetryingTest(2)
    public void germanUrl() {
        HomePageObject.storeAndOpenSavedUrl("http://www.travemünde.de/");
        TravemundeDe.BODY.should(exist);
    }

    /**
     * Stores estonian URL.
     */
    @RetryingTest(2)
    public void estonianUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://sõnaveeb.ee");
        SonaveebEe.LOGO.shouldBe(visible);
        SonaveebEe.LOGO.shouldHave(attribute("alt", SonaveebEe.LOGO_ALT_TEXT));
    }

    /**
     * Multiple languages.
     */
    @RetryingTest(2)
    public void multiLanguageUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://€.linux.it");

        // verify that opens Euro Linux Page
        SelenideElement h1 = $(EuroLinuxIt.H1);
        h1.should(exist);
        h1.shouldHave(text(EuroLinuxIt.H1_TEXT));
    }
}
