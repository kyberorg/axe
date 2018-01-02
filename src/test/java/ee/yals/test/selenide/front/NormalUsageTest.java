package ee.yals.test.selenide.front;

import com.codeborne.selenide.WebDriverRunner;
import ee.yals.test.selenide.UITest;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;
import static ee.yals.Endpoint.AUTH_PAGE;
import static ee.yals.test.utils.selectors.FrontSelectors.AuthRow.LOGIN_BUTTON;
import static org.junit.Assert.assertTrue;

/**
 * Normal front page usage, which need only single action (click, input etc).
 * Other tests located within {@link MultiStepTest}
 *
 * @since 3.0
 */
public class NormalUsageTest extends UITest {

    @Test
    public void clickOnLoginButtonOpensAuthPage() {
        open("/");
        LOGIN_BUTTON.click();
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl.trim().contains(AUTH_PAGE));
    }
}
