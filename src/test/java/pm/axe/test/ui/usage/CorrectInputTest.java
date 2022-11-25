package pm.axe.test.ui.usage;

import pm.axe.Endpoint;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tries to input valid values and checks returned result.
 *
 * @since 1.0
 */
public class CorrectInputTest extends SelenideTest {

    /**
     * Test setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Stores http link.
     */
    @Test
    public void httpLink() {
        String link = "http://http.yadev.eu";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    /**
     * Stores https link.
     */
    @Test
    public void httpsLink() {
        String link = "https://github.com/virtalab";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    /**
     * Stores ftp link.
     */
    @Test
    public void ftpLink() {
        String link = "ftp://ftp.yandex.ru";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    /**
     * Stores link with cyrillic letters.
     */
    @Test
    public void cyrillicLink() {
        String link = "http://президент.рф";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }


    /**
     * Correctly stores and uses link from Russian Wiki (one with cyrillic letters).
     */
    @Test
    public void linkFromRussianWikipedia() {
        String link =
                "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8"
                        + "_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9"
                        + "_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9"
                        + "_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%"
                        + "81%D0%BA%D0%BE%D0%B9"
                        + "_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    /**
     * Stores very long link from Facebook.
     */
    @Test
    public void veryLongLinkFromFacebook() {
        String link = "https://www.facebook.com/kallaskaja/"
                + "?__cft__%5B0%5D=AZWCTMuD7IisNVDDgkRkbaKveUKUuFfnAXKNfKVON"
                + "_JhRowIA8EJkAjmqU25goOf5OQFuUPLND19WYbO-njrGBA_xxRNkBUDwGqivdDx_bixKxXI7fq8rR9V"
                + "_neqU3MkuB5OmNKJEPjsrMeTwMDLn2heVAIDHV176G6qhrzf1kZdlHZ0F2NdPRz3AceR4W64MtmgblwzwVCgrib"
                + "Q4sijefQl&__tn__=kC%2CP-R";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void checkExpectedBehavior() {
        HomePageObject.ResultArea.RESULT_AREA.shouldBe(visible);
        HomePageObject.ResultArea.RESULT_LINK.shouldBe(visible);
        HomePageObject.ResultArea.RESULT_LINK.shouldHave(text(APP_SHORT_URL));
        HomePageObject.ResultArea.COPY_LINK_ICON.shouldBe(visible);

        String actualText = HomePageObject.ResultArea.RESULT_LINK.getText();
        String hrefValue = HomePageObject.ResultArea.RESULT_LINK.getAttribute("href");
        assertEquals(actualText, hrefValue, "link in 'href' value is not same as link shown text");

        HomePageObject.QrCodeArea.QR_CODE_AREA.shouldBe(visible);
        HomePageObject.QrCodeArea.QR_CODE.shouldBe(visible);
        Assertions.assertTrue(HomePageObject.QrCodeArea.QR_CODE.isImage(), "QR code is not image");

        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_AREA.shouldBe(visible);
        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_TEXT.shouldBe(visible);
        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_LINK.shouldBe(visible);
        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_POST_TEXT.shouldBe(visible);

        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_TEXT.shouldHave(text("link")).shouldHave(text("QR Code"));
        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_POST_TEXT.shouldHave(text("page"));

        HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_LINK.shouldHave(text("My Links"));
        String myLinksNoteLinkHref = HomePageObject.MyLinksNoteArea.MY_LINKS_NOTE_LINK.getAttribute("href");
        String myLinksPagePath = Endpoint.UI.MY_LINKS_PAGE;
        assert myLinksNoteLinkHref != null;
        assertTrue(myLinksNoteLinkHref.endsWith(myLinksPagePath), "link in 'href' should lead to MyLinks page");

        HomePageObject.MainArea.LongURLInput.INPUT.shouldBe(empty);

        HomePageObject.ErrorModal.ERROR_MODAL.shouldNot(exist);
    }
}
