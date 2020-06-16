package eu.yals.test.ui.usage;

import eu.yals.test.TestUtils;
import eu.yals.test.ui.HomePageTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
public class CorrectInputTestIT extends HomePageTest {

    @Test
    public void httpLink() {
        openHomePage();
        String link = "http://http.yadev.eu";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void httpsLink() {
        openHomePage();
        String link = "https://github.com/virtalab";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void ftpLink() {
        openHomePage();
        String link = "ftp://ftp.yandex.ru";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void cyrillicLink() {
        openHomePage();
        String link = "http://президент.рф";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkWithoutProtocol() {
        openHomePage();
        String link = "www.kv.ee/2992207";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkFromRussianWikipedia() {
        openHomePage();
        String link =
                "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void veryLongLinkFromFacebook() {
        openHomePage();
        String link = "https://www.facebook.com/kallaskaja/?__cft__%5B0%5D=AZWCTMuD7IisNVDDgkRkbaKveUKUuFfnAXKNfKVON_JhRowIA8EJkAjmqU25goOf5OQFuUPLND19WYbO-njrGBA_xxRNkBUDwGqivdDx_bixKxXI7fq8rR9V_neqU3MkuB5OmNKJEPjsrMeTwMDLn2heVAIDHV176G6qhrzf1kZdlHZ0F2NdPRz3AceR4W64MtmgblwzwVCgribQ4sijefQl&__tn__=kC%2CP-R";
        homeView.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void checkExpectedBehavior() {
        $$(homeView.getResultArea()).shouldBeDisplayed();
        $$(homeView.getShortLink()).shouldBeDisplayed();
        $$(homeView.getShortLink()).shouldHaveText(BASE_URL);

        $$(homeView.getCopyLinkButton()).shouldBeDisplayed();

        String actualText = homeView.getShortLink().getText();
        String hrefValue = homeView.getShortLink().getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        $$(homeView.getQRCodeArea()).shouldBeDisplayed();
        $$(homeView.getQRCode()).shouldBeDisplayed();
        assertTrue("QR code is not image", $$(homeView.getQRCode()).isImage());

        TestUtils.assertEmpty("Log URL Input is not empty", homeView.getInput().getValue());

        assertThatErrorNotificationIsNotVisible();
    }
}
