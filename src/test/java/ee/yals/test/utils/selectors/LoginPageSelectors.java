package ee.yals.test.utils.selectors;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from loginPage.ftl
 *
 * @since 3.0
 */
public class LoginPageSelectors {
    public static class Form {
        public static final SelenideElement HEADING = $(".login-heading");
        public static final SelenideElement SUB_HEADING = $(".login-subheading");
        public static final SelenideElement USER_INPUT = $("#username");
        public static final SelenideElement PASSWORD_INPUT = $("#password");
        public static final SelenideElement DEMO_STRING = $("#demoString");
        public static final SelenideElement LOGIN_BUTTON = $("#loginButton");
    }
}
