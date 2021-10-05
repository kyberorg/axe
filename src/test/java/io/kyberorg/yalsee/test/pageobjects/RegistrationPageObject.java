package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.TextFieldElement;
import io.kyberorg.yalsee.ui.user.RegistrationView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link RegistrationView}.
 *
 * @since 4.0
 */
public final class RegistrationPageObject {

    private RegistrationPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public final static SelenideElement PAGE_ID = $("#" + RegistrationView.IDs.PAGE_ID);

    public static final SelenideElement FORM_TITLE = $("#" + RegistrationView.IDs.FORM_TITLE);

    public static final TextFieldElement USERNAME_INPUT =
            TextFieldElement.byCss("#" + RegistrationView.IDs.USERNAME_INPUT);


    public static final SelenideElement EMAIL_INPUT = $("#" + RegistrationView.IDs.EMAIL_INPUT);
    public static final SelenideElement TELEGRAM_INPUT = $("#" + RegistrationView.IDs.TELEGRAM_INPUT);
    public static final SelenideElement SAME_AS_USERNAME_CHECKBOX =
            $("#" + RegistrationView.IDs.SAME_AS_USERNAME_CHECKBOX);
    public static final SelenideElement PASSWORD_INPUT = $("#" + RegistrationView.IDs.PASSWORD_INPUT);
    public static final SelenideElement REPEAT_PASSWORD_INPUT =
            $("#" + RegistrationView.IDs.REPEAT_PASSWORD_INPUT);
    public static final SelenideElement SUBMIT_BUTTON = $("#" + RegistrationView.IDs.SUBMIT_BUTTON);

}
