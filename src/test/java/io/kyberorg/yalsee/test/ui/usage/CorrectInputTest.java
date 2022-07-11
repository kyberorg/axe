package io.kyberorg.yalsee.test.ui.usage;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject.MainArea.LongURLInput;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ErrorModal.ERROR_MODAL;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.MyLinksNoteArea.*;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.QrCodeArea.QR_CODE;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static io.kyberorg.yalsee.test.pageobjects.HomePageObject.ResultArea.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
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
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
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
        RESULT_AREA.shouldBe(visible);
        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.shouldHave(text(APP_SHORT_URL));
        COPY_LINK_ICON.shouldBe(visible);

        String actualText = RESULT_LINK.getText();
        String hrefValue = RESULT_LINK.getAttribute("href");
        assertEquals(actualText, hrefValue, "link in 'href' value is not same as link shown text");

        QR_CODE_AREA.shouldBe(visible);
        QR_CODE.shouldBe(visible);
        assertTrue(QR_CODE.isImage(), "QR code is not image");

        MY_LINKS_NOTE_AREA.shouldBe(visible);
        MY_LINKS_NOTE_TEXT.shouldBe(visible);
        MY_LINKS_NOTE_LINK.shouldBe(visible);
        MY_LINKS_NOTE_POST_TEXT.shouldBe(visible);

        MY_LINKS_NOTE_TEXT.shouldHave(text("link")).shouldHave(text("QR Code"));
        MY_LINKS_NOTE_POST_TEXT.shouldHave(text("page"));

        MY_LINKS_NOTE_LINK.shouldHave(text("My Links"));
        String myLinksNoteLinkHref = MY_LINKS_NOTE_LINK.getAttribute("href");
        String myLinksPagePath = Endpoint.UI.MY_LINKS_PAGE;
        assert myLinksNoteLinkHref != null;
        assertTrue(myLinksNoteLinkHref.endsWith(myLinksPagePath), "link in 'href' should lead to MyLinks page");

        LongURLInput.INPUT.shouldBe(empty);

        ERROR_MODAL.shouldNot(exist);
    }
}
