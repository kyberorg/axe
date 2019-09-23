package eu.yals.test.ui.pages.front;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.FrontPage;
import eu.yals.test.ui.pageobjects.external.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.*;

/**
 * Contains IDN URL multi step tests for Front page
 *
 * @since 2.5
 */
@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IdnTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void russianUrl() {
        pasteValueInFormAndSubmitIt("http://кто.рф");

        openSavedUrl();

        //verify that KtoRF opened
        SelenideElement eggs = KtoRf.DIV_EGGS;
        eggs.shouldBe(exist);
    }

    @Test
    public void swedishUrl() {
        pasteValueInFormAndSubmitIt("https://räksmörgås.josefsson.org");

        openSavedUrl();

        //verify that swedish site opened
        SelenideElement h1 = JosefssonOrg.H1;
        h1.should(exist);
        String h1Text = h1.text();
        assertNotNull(h1Text);
        assertTrue(h1Text.contains(JosefssonOrg.H1_TEXT));
    }

    @Test
    public void finnishUrl() {
        pasteValueInFormAndSubmitIt("https://sää.fi");

        openSavedUrl();

        //verify that finnish sire opened (currently redirect to foreca.fi)
        SelenideElement logo = ForecaFi.LOGO;
        assertNotNull(logo);
        logo.should(exist);
        logo.should(have(attribute("title")));
        String titleAttributeOfLogoLink = logo.attr("title");
        assertEquals(ForecaFi.LOGO_TITLE, titleAttributeOfLogoLink);
    }

    @Test
    public void arabicUrl() {
        pasteValueInFormAndSubmitIt("http://موقع.وزارة-الاتصالات.مصر/");

        openSavedUrl();

        //verify that opens page of IT ministry of Egypt
        Assert.assertEquals(EgyptianMinistryOfIT.getTitleText(), Selenide.title());
    }

    @Test
    public void taiwaneseUrl() {
        pasteValueInFormAndSubmitIt("http://中文.tw/");

        openSavedUrl();

        SelenideElement navTable = ZhongwenTw.NAV_TABLE;
        assertNotNull(navTable);
        navTable.should(exist);
    }

    @Test
    public void norseUrl() {
        pasteValueInFormAndSubmitIt("http://www.nårsk.no/");

        openSavedUrl();

        assertEquals(NorskNo.getTitleText(), Selenide.title());
    }

    @Test
    public void polishUrl() {
        pasteValueInFormAndSubmitIt("http://żółć.pl");

        openSavedUrl();

        assertEquals(ZolcPl.getTitleText(), Selenide.title());
    }

    @Test
    public void germanUrl() {
        pasteValueInFormAndSubmitIt("http://www.travemünde.de/");

        openSavedUrl();

        assertEquals(TravemundeDe.getTitleText(), Selenide.title());
    }

    @Test
    public void estonianUrl() {
        pasteValueInFormAndSubmitIt("https://sõnaveeb.ee");

        openSavedUrl();

        assertEquals(SonaveebEe.getTitleText(), Selenide.title());
    }

    @Test
    public void multiLanguageUrl() {
        pasteValueInFormAndSubmitIt("http://€.linux.it");

        openSavedUrl();

        //verify that opens Euro Linux Page
        SelenideElement h1 = EuroLinuxIt.H1;
        assertNotNull(h1);
        h1.should(exist);
        h1.should(have(text(EuroLinuxIt.H1_TEXT)));
    }

    private void openSavedUrl() {
        String shortLink = FrontPage.ResultRow.RESULT_LINK.text();
        open(shortLink);
    }
}
