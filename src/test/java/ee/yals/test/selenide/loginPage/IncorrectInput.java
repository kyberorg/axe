package ee.yals.test.selenide.loginPage;

import com.codeborne.selenide.WebDriverRunner;
import ee.yals.Endpoint;
import ee.yals.test.selenide.UITest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;
import static ee.yals.Endpoint.LOGIN_FORM;
import static ee.yals.test.utils.selectors.LoginPageSelectors.Form.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests UI on login page with some incorrect scenarios
 *
 * @since 3.0
 */
public class IncorrectInput extends UITest {

    @Before
    public void openUrl() {
        open(Endpoint.LOGIN_FORM);
    }

    @Test
    public void onClickWithoutUsernameAndPasswordShouldStayAtSamePage() {
        LOGIN_BUTTON.click();
        assertSameUrl();
    }

    @Test
    public void onClickWithUsernameButWithoutPasswordShouldStayAtSamePage() {
        USER_INPUT.setValue("xyz");
        LOGIN_BUTTON.click();
        assertSameUrl();
    }

    @Test
    public void onClickWithPasswordButWithoutUsernameShouldStayAtSamePage() {
        PASSWORD_INPUT.setValue("xyz");
        LOGIN_BUTTON.click();
        assertSameUrl();
    }

    @Test
    public void onClickWithSpaceUsernameAndWithPasswordShouldStayAtSamePage() {
        USER_INPUT.setValue(" ");
        LOGIN_BUTTON.click();
        assertSameUrl();
    }

    private void assertSameUrl() {
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl.trim().contains(LOGIN_FORM));
    }
}
