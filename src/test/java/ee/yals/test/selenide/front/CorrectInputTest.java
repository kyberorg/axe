package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.selenide.UITest.pasteValueInFormAndSubmitIt;
import static ee.yals.test.utils.selectors.FrontSelectors.ErrorRow.ERROR_MODAL;
import static ee.yals.test.utils.selectors.FrontSelectors.ErrorRow.ERROR_TEXT;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.selectors.FrontSelectors.ResultRow.*;
import static org.junit.Assert.assertEquals;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CorrectInputTest {

    @BeforeClass
    public static void setUp() {
        UITest.setUp();
    }

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

    @AfterClass
    public static void tearDown() {
        UITest.tearDown();
    }

    private void checkExpectedBehavior() {
        RESULT_DIV.shouldBe(visible);
        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.shouldHave(text(UITest.BASE_URL));
        COPY_RESULT_ICON.shouldBe(visible);
        String actualText = RESULT_LINK.getText();
        String hrefValue = RESULT_LINK.getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        LONG_URL_INPUT.shouldBe(empty);

        ERROR_MODAL.shouldNotBe(visible);
        ERROR_TEXT.shouldBe(empty);
    }
}
