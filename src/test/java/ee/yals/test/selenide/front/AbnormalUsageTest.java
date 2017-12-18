package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import org.junit.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tests unusual usage
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.0
 */
public class AbnormalUsageTest extends UITest {

    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String EXTRA_ARGUMENT = "mineMetsa";
        final String LINK_TO_SAVE = "https://vr.fi";

        open("/?" + EXTRA_ARGUMENT);
        pasteValueInFormAndSubmitIt(LINK_TO_SAVE);

        $("#resultLink").shouldNotBe(empty);
        $("#resultLink").shouldNotHave(text(EXTRA_ARGUMENT));
    }

    private void pasteValueInFormAndSubmitIt(String link) {
        $("#longUrl").setValue(link);
        $("form").find("button").click();
    }
}
