package ee.yals.test.selenide.loginPage;

import com.codeborne.selenide.WebDriverRunner;
import ee.yals.Endpoint;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.selectors.LoginPageSelectors.Form.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests login page with correct information
 *
 * @since 3.0
 */
public class CorrectInput extends UITest {

    @Before
    public void openUrl() {
        open(Endpoint.LOGIN_FORM);
    }

    @Test
    @Ignore //as Endpoint is not implemented
    public void demoDemoLoginShouldRedirectToMain() {
        USER_INPUT.setValue("demo");
        PASSWORD_INPUT.setValue("demo");
        LOGIN_BUTTON.click();
        assertMainPage();
    }

    private void assertMainPage() {
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl.trim().equals(Endpoint.SLASH_BASE));
    }
}
