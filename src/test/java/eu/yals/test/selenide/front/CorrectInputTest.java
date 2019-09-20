package eu.yals.test.selenide.front;

import eu.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.utils.pages.FrontPage.ErrorRow.ERROR_MODAL;
import static eu.yals.test.utils.pages.FrontPage.ErrorRow.ERROR_TEXT;
import static eu.yals.test.utils.pages.FrontPage.MainRow.LONG_URL_INPUT;
import static eu.yals.test.utils.pages.FrontPage.ResultRow.*;
import static org.junit.Assert.assertEquals;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CorrectInputTest extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void httpLink() {
        String link = "http://virtadev.net";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void httpsLink() {
        String link = "https://github.com/virtalab";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void ftpLink() {
        String link = "ftp://ftp.yandex.ru";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void cyrillicLink() {
        String link = "http://президент.рф";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkWithoutProtocol() {
        String link = "www.kv.ee/2992207";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void linkFromRussianWikipedia() {
        String link = "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void checkExpectedBehavior() {
        RESULT_DIV.shouldBe(visible);
        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.shouldHave(text(BASE_URL));
        COPY_RESULT_ICON.shouldBe(visible);
        String actualText = RESULT_LINK.getText();
        String hrefValue = RESULT_LINK.getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        LONG_URL_INPUT.shouldBe(empty);

        ERROR_MODAL.shouldNotBe(visible);
        ERROR_TEXT.shouldBe(empty);
    }
}
