package pm.axe.test.ui.usage;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.SelenideUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junitpioneer.jupiter.RetryingTest;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.pageobjects.external.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

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
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Stores russian URL.
     */
    @RetryingTest(2)
    public void russianUrl() {
        HomePageObject.storeAndOpenSavedUrl("http://кто.рф");
        // verify that KtoRF opened
        SelenideElement regDiv = Selenide.$(KtoRf.DIV_REG);
        regDiv.should(exist);
    }

    /**
     * Stores finnish URL.
     */
    @RetryingTest(2)
    public void finnishUrl() {
        HomePageObject.storeAndOpenSavedUrl("https://sää.fi");
        SelenideElement logo = Selenide.$(ForecaFi.LOGO);
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
        SelenideElement uniqueClassElement = Selenide.$(ArabUrlRegistrar.MAIN_CLASS);
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

        SelenideElement navTable = Selenide.$(ZhongwenTw.NAV_TABLE);
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
