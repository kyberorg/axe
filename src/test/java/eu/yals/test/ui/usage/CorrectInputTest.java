package eu.yals.test.ui.usage;

import eu.yals.test.ui.SelenideTest;
import eu.yals.test.ui.pageobjects.uus.HomePageObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ErrorModal.ERROR_MODAL;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.MainArea.LONG_URL_INPUT;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.QrCodeArea.QR_CODE;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ResultArea.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
@SpringBootTest
public class CorrectInputTest extends SelenideTest {
    @Before
    public void beforeTest() {
        open("/");
        updateTestNameAndStartVideo();
    }

    @Test
    public void httpLink() {
        String link = "http://http.yadev.eu";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void httpsLink() {
        String link = "https://github.com/virtalab";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void ftpLink() {
        String link = "ftp://ftp.yandex.ru";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void cyrillicLink() {
        String link = "http://президент.рф";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkWithoutProtocol() {
        String link = "www.kv.ee/2992207";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkFromRussianWikipedia() {
        String link =
                "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void veryLongLinkFromFacebook() {
        String link = "https://www.facebook.com/kallaskaja/?__cft__%5B0%5D=AZWCTMuD7IisNVDDgkRkbaKveUKUuFfnAXKNfKVON_JhRowIA8EJkAjmqU25goOf5OQFuUPLND19WYbO-njrGBA_xxRNkBUDwGqivdDx_bixKxXI7fq8rR9V_neqU3MkuB5OmNKJEPjsrMeTwMDLn2heVAIDHV176G6qhrzf1kZdlHZ0F2NdPRz3AceR4W64MtmgblwzwVCgribQ4sijefQl&__tn__=kC%2CP-R";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void checkExpectedBehavior() {
        RESULT_AREA.shouldBe(visible);
        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.shouldHave(text(BASE_URL));
        COPY_LINK_ICON.shouldBe(visible);

        String actualText = RESULT_LINK.getText();
        String hrefValue = RESULT_LINK.getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        QR_CODE_AREA.shouldBe(visible);
        QR_CODE.shouldBe(visible);
        assertTrue("QR code is not image", QR_CODE.isImage());

        LONG_URL_INPUT.shouldBe(empty);

        ERROR_MODAL.shouldNot(exist);
    }
}
