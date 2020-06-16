package eu.yals.test.ui.usage;

import eu.yals.test.ui.HomePageTest;
import eu.yals.test.ui.pageobjects.external.*;
import eu.yals.test.utils.elements.YalsElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * Contains IDN URL multi step tests for Front page.
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public class IdnTestIT extends HomePageTest {

    @Test
    public void russianUrl() {
        openHomePage();
        storeAndOpenSavedUrl("http://кто.рф");

        // verify that KtoRF opened
        YalsElement eggs = $$(KtoRf.DIV_EGGS);
        eggs.shouldExist();
    }

    @Test
    public void finnishUrl() {
        openHomePage();
        storeAndOpenSavedUrl("https://sää.fi");
        YalsElement logo = $$(ForecaFi.LOGO);
        logo.shouldExist();
        logo.shouldHaveAttr("title", ForecaFi.LOGO_TITLE);
    }

    @Test
    public void arabicUrl() {
        openHomePage();
        storeAndOpenSavedUrl("https://www.101domain.com/عرب.htm");

        //needed because site site loads way too long
        waitUntilSiteLoads(40);

        // verify that opens page of Registation of arabic names
        YalsElement uniqueClassElement = $$(ArabUrlRegistrar.MAIN_CLASS);
        uniqueClassElement.shouldExist();
    }

    @Test
    public void taiwaneseUrl() {
        openHomePage();
        storeAndOpenSavedUrl("http://中文.tw/");

        YalsElement navTable = $$(ZhongwenTw.NAV_TABLE);
        navTable.shouldExist();
    }

    @Test
    public void germanUrl() {
        openHomePage();
        storeAndOpenSavedUrl("http://www.travemünde.de/");
        Assert.assertEquals(TravemundeDe.TITLE_TEXT, getPageTitle());
    }

    @Test
    public void estonianUrl() {
        openHomePage();
        storeAndOpenSavedUrl("https://sõnaveeb.ee");
        Assert.assertEquals(SonaveebEe.TITLE_TEXT, getPageTitle());
    }

    /**
     * Multiple languages.
     */
    @Test
    public void multiLanguageUrl() {
        openHomePage();
        storeAndOpenSavedUrl("http://€.linux.it");

        // verify that opens Euro Linux Page
        YalsElement h1 = $$(EuroLinuxIt.H1);
        h1.shouldExist();
        h1.shouldHaveText(EuroLinuxIt.H1_TEXT);
    }

    private void storeAndOpenSavedUrl(String urlToStore) {
        homeView.pasteValueInFormAndSubmitIt(urlToStore);
        String shortLink = homeView.getSavedUrl();
        open(shortLink);
    }

    private String getPageTitle() {
        return getDriver().getTitle();
    }
}
