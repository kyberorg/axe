package eu.yals.test.ui;

import com.codeborne.selenide.Condition;
import eu.yals.test.ui.pageobjects.NewHomePageObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;

@RunWith(SpringRunner.class)
public class AbnormalUsageTest extends SelenideTest {
    @Before
    public void beforeTest() {
        open("/");
        updateTestName();
    }

    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String EXTRA_ARGUMENT = "mineMetsa";
        final String LINK_TO_SAVE = "https://vr.fi";

        open("/?" + EXTRA_ARGUMENT);

        NewHomePageObject.pasteValueInFormAndSubmitIt(LINK_TO_SAVE);

        NewHomePageObject.ResultArea.RESULT_LINK.shouldNotBe(empty);
        NewHomePageObject.ResultArea.RESULT_LINK.shouldNotHave(text(EXTRA_ARGUMENT));
    }
}
