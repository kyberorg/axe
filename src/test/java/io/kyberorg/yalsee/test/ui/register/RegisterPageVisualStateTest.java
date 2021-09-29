package io.kyberorg.yalsee.test.ui.register;

import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.special.RedirectView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.RegistrationPageObject.*;

/**
 * Testing Visual State of {@link RedirectView}.
 *
 * @since 4.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class RegisterPageVisualStateTest extends SelenideTest {

    @BeforeAll
    public static void beforeTests() {
        open("/register");
    }

    @Test
    public void allFormFieldsAreVisible() {
        FORM_TITLE.should(exist);
        FORM_TITLE.shouldBe(visible);

        USERNAME_INPUT.getInput().should(exist);
        USERNAME_INPUT.getInput().shouldBe(visible);

        EMAIL_INPUT.should(exist);
        EMAIL_INPUT.shouldBe(visible);

        TELEGRAM_INPUT.should(exist);
        TELEGRAM_INPUT.shouldBe(visible);

        SAME_AS_USERNAME_CHECKBOX.should(exist);
        SAME_AS_USERNAME_CHECKBOX.shouldBe(visible);

        PASSWORD_INPUT.should(exist);
        PASSWORD_INPUT.shouldBe(visible);

        REPEAT_PASSWORD_INPUT.should(exist);
        REPEAT_PASSWORD_INPUT.shouldBe(visible);

        SUBMIT_BUTTON.should(exist);
        SUBMIT_BUTTON.shouldBe(visible);
    }
}
