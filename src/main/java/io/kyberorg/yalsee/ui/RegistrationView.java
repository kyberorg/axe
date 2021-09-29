package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import java.util.stream.Stream;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationView extends YalseeLayout {

    private final VerticalLayout form = new VerticalLayout();
    private H2 formTitle;
    private TextField usernameInput;

    private HorizontalLayout confirmationMethodFields;
    private VerticalLayout confirmationMethodSection;
    private EmailField emailInput;
    private TextField telegramInput;
    private Checkbox sameAsUsername;

    private HorizontalLayout passwordFields;
    private VerticalLayout passwordSection;
    private PasswordField passwordField;
    private PasswordField repeatPasswordField;
    private Button submitButton;


    public RegistrationView() {
        setId(IDs.PAGE_ID);

        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        formTitle = new H2("Register");
        formTitle.setId(IDs.FORM_TITLE);

        usernameInput = new TextField("username");
        usernameInput.setId(IDs.USERNAME_INPUT);

        Label confirmationMethodLabel = new Label("Confirmation method");

        emailInput = new EmailField("email");
        emailInput.setId(IDs.EMAIL_INPUT);

        telegramInput = new TextField("telegram");
        telegramInput.setId(IDs.TELEGRAM_INPUT);

        sameAsUsername = new Checkbox("same as username");
        sameAsUsername.setId(IDs.SAME_AS_USERNAME_CHECKBOX);

        VerticalLayout telegramFields = new VerticalLayout(telegramInput, sameAsUsername);

        confirmationMethodFields = new HorizontalLayout(emailInput, telegramFields);
        confirmationMethodSection = new VerticalLayout(confirmationMethodLabel, confirmationMethodFields);

        Label passwordSectionLabel = new Label("Password");

        passwordField = new PasswordField("password");
        passwordField.setId(IDs.PASSWORD_INPUT);

        repeatPasswordField = new PasswordField("repeat password");
        repeatPasswordField.setId(IDs.REPEAT_PASSWORD_INPUT);

        passwordFields = new HorizontalLayout(passwordField, repeatPasswordField);

        passwordSection = new VerticalLayout(passwordSectionLabel, passwordFields);

        submitButton = new Button("submit");
        submitButton.setId(IDs.SUBMIT_BUTTON);

        form.add(formTitle, usernameInput, confirmationMethodSection, passwordSection, submitButton);
        add(form);
    }

    private void applyStyle() {
        Stream<HasSize> inputs = Stream.of(this.usernameInput);
        inputs.forEach(e -> e.setWidth("50%"));

        Stream<HasSize> fullSizeElements = Stream.of(confirmationMethodSection, confirmationMethodFields,
                emailInput, telegramInput, passwordField, repeatPasswordField,
                passwordFields, passwordSection, submitButton);
        fullSizeElements.forEach(HasSize::setWidthFull);
    }

    private void applyLoadState() {
        usernameInput.setAutofocus(true);
    }

    public static class IDs {
        public static final String PAGE_ID = "registerPage";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String EMAIL_INPUT = "emailInput";
        public static final String TELEGRAM_INPUT = "telegramInput";
        public static final String SAME_AS_USERNAME_CHECKBOX = "sameAsUsernameCheckbox";
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REPEAT_PASSWORD_INPUT = "repeatPasswordInput";
        public static final String SUBMIT_BUTTON = "submitButton";
        public static final String FORM_TITLE = "formTitle";
    }
}
