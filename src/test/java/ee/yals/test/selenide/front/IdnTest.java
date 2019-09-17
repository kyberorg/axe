package ee.yals.test.selenide.front;

import com.codeborne.selenide.SelenideElement;
import ee.yals.test.selenide.UITest;
import ee.yals.test.utils.pages.JosefssonOrg;
import ee.yals.test.utils.pages.KtoRf;
import ee.yals.test.utils.pages.external.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.pages.FrontSelectors.ResultRow.RESULT_LINK;
import static org.junit.Assert.*;

/**
 * Contains IDN URL multi step tests for Front page
 *
 * @since 2.5
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IdnTest extends UITest {

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
        pasteValueInFormAndSubmitIt("https://säa.fi");

        openSavedUrl();

        //verify that finnish sire opened (currently redirect to foreca.fi)
        SelenideElement logo = Foreca.LOGO;
        assertNotNull(logo);
        logo.should(exist);
        logo.should(have(attribute("title")));
        String titleAttributeOfLogoLink = logo.attr("title");
        assertEquals(Foreca.LOGO_TITLE, titleAttributeOfLogoLink);
    }

    @Test
    public void arabicUrl() {
        pasteValueInFormAndSubmitIt("http://موقع.وزارة-الاتصالات.مصر/");

        openSavedUrl();

        //verify that opens page of IT ministry of Egypt
        SelenideElement title = EgyptianMinistryOfIT.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(EgyptianMinistryOfIT.TITLE_TEXT)));
    }

    @Test
    public void hindiUrl() {
        pasteValueInFormAndSubmitIt("http://महरोत्रा.com");

        openSavedUrl();

        SelenideElement title = Hindi.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(Hindi.TITLE_TEXT)));
    }

    @Test
    public void taiwaneseUrl() {
        pasteValueInFormAndSubmitIt("http://中文.tw/");

        openSavedUrl();

        SelenideElement navTable = Taiwan.NAV_TABLE;
        assertNotNull(navTable);
        navTable.should(exist);
    }

    @Test
    public void norseUrl() {
        pasteValueInFormAndSubmitIt("http://www.nårsk.no/");

        openSavedUrl();

        SelenideElement title = Norsk.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(Norsk.TITLE_TEXT)));
    }

    @Test
    public void polishUrl() {
        pasteValueInFormAndSubmitIt("http://żółć.pl");

        openSavedUrl();

        SelenideElement title = Zolc.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(Zolc.TITLE_TEXT)));
    }

    @Test
    public void germanUrl() {
        pasteValueInFormAndSubmitIt("http://www.travemünde.de/");

        openSavedUrl();

        SelenideElement title = Travemunde.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(Travemunde.TITLE_TEXT)));
    }

    @Test
    public void estonianUrl() {
        pasteValueInFormAndSubmitIt("https://sõnaveeb.ee");

        openSavedUrl();

        SelenideElement title = Sonaveeb.TITLE;
        assertNotNull(title);
        title.should(exist);
        title.should(have(text(Sonaveeb.TITLE_TEXT)));

    }

    @Test
    public void multiLanguageUrl() {
        pasteValueInFormAndSubmitIt("");

        openSavedUrl();

        //verify that opens Euro Linux Page
        SelenideElement h1 = EuroLinux.H1;
        assertNotNull(h1);
        h1.should(exist);
        h1.should(have(text(EuroLinux.H1_TEXT)));
    }

    private void openSavedUrl() {
        String shortLink = RESULT_LINK.text();
        open(shortLink);
    }
}
