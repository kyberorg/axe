package eu.yals.test.ui.usage;

import eu.yals.test.pageobjects.HomePageObject;
import eu.yals.test.ui.SelenideTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;

/**
 * Simulates some not normal user activity
 *
 * @since 1.0
 */
@SpringBootTest
public class AbnormalUsageTest extends SelenideTest {
    @Before
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
    }

    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String EXTRA_ARGUMENT = "mineMetsa";
        final String LINK_TO_SAVE = "https://vr.fi";

        open("/?" + EXTRA_ARGUMENT);

        HomePageObject.pasteValueInFormAndSubmitIt(LINK_TO_SAVE);

        HomePageObject.ResultArea.RESULT_LINK.shouldNotBe(empty);
        HomePageObject.ResultArea.RESULT_LINK.shouldNotHave(text(EXTRA_ARGUMENT));
    }
}
