package ee.yals.test.selenide.front;

import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.assertEquals;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
public class CorrectInput extends UITest {

    @Rule // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenShotsOnFail = ScreenShooter.failedTests();

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void httpLink(){
        String link = "http://virtadev.net";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void httpsLink(){
        String link = "https://github.com/virtalab";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void ftpLink(){
        String link = "ftp://ftp.yandex.ru";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    @Test
    public void cyrillicLink(){
        String link = "http://президент.рф";
        pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void pasteValueInFormAndSubmitIt(String link){
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }

    private void checkExpectedBehavior(){
        $("#result").shouldBe(visible);
        $("#resultLink").shouldBe(visible);
        $("#resultLink").shouldHave(text(BASE_URL));
        String actualText = $("#resultLink").getText();
        String hrefValue = $("#resultLink").getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        $("#longUrl").shouldBe(empty);

        $("#error").shouldNotBe(visible);
        $("#errorText").shouldBe(empty);
    }
}
