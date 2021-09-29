package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.components.ConfirmationMethodsLayout;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import java.util.stream.Stream;

@SpringComponent
@UIScope
@CssImport(value = "./css/registration_view.css")
@CssImport(value = "./css/registration_view_form.css", themeFor = "vaadin-form-item")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationView extends YalseeLayout {

    private final VerticalLayout form = new VerticalLayout();
    private H2 formTitle;

    private VerticalLayout usernameSection;
    private FormLayout usernameFields;
    private TextField usernameInput;

    private VerticalLayout confirmationMethodSection;
    private ConfirmationMethodsLayout confirmationMethodFields;
    private EmailField emailInput;
    private TextField telegramInput;
    private Checkbox sameAsUsername;

    private FormLayout passwordFields;
    private VerticalLayout passwordSection;
    private PasswordField passwordField;
    private PasswordField repeatPasswordField;
    private Button submitButton;

    private final static String START_POINT = "1px";
    private final static String BREAKPOINT = "508px";


    public RegistrationView() {
        setId(IDs.PAGE_ID);

        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        form.setId(IDs.FORM);

        formTitle = new H2("Register");
        formTitle.setId(IDs.FORM_TITLE);

        usernameInput = new TextField();
        usernameInput.setId(IDs.USERNAME_INPUT);

        usernameFields = new FormLayout();
        usernameFields.addFormItem(usernameInput, "Username");

        usernameSection = new VerticalLayout(usernameFields);

        Label confirmationMethodLabel = new Label("Confirmation method");

        emailInput = new EmailField();
        emailInput.setId(IDs.EMAIL_INPUT);

        telegramInput = new TextField();
        telegramInput.setId(IDs.TELEGRAM_INPUT);

        sameAsUsername = new Checkbox("Same as username");
        sameAsUsername.setId(IDs.SAME_AS_USERNAME_CHECKBOX);

        confirmationMethodFields = new ConfirmationMethodsLayout();
        confirmationMethodFields.addItemWithLabel("E-mail", emailInput);
        confirmationMethodFields.addItemWithLabel("Telegram", telegramInput, sameAsUsername);

        confirmationMethodSection = new VerticalLayout(confirmationMethodLabel, confirmationMethodFields);

        Label passwordSectionLabel = new Label("Password (optional)");

        passwordField = new PasswordField();
        passwordField.setId(IDs.PASSWORD_INPUT);

        repeatPasswordField = new PasswordField();
        repeatPasswordField.setId(IDs.REPEAT_PASSWORD_INPUT);

        passwordFields = new FormLayout();
        passwordFields.addFormItem(passwordField, "Password");
        passwordFields.addFormItem(repeatPasswordField, "Same Password");

        passwordSection = new VerticalLayout(passwordSectionLabel, passwordFields);

        submitButton = new Button("submit");
        submitButton.setId(IDs.SUBMIT_BUTTON);

        form.add(formTitle, usernameSection, confirmationMethodSection, passwordSection, submitButton);
        add(form);
    }

    private void applyStyle() {
        form.getStyle().set("background", "white");
        form.setMaxWidth(761, Unit.PIXELS);

        confirmationMethodSection.setClassName("compact-section");
        passwordSection.setClassName("compact-section");

        Stream<FormLayout> forms = Stream.of(usernameFields, confirmationMethodFields.getContent(), passwordFields);
        forms.forEach(form -> form.setResponsiveSteps(
                new FormLayout.ResponsiveStep(START_POINT, 1),
                new FormLayout.ResponsiveStep(BREAKPOINT, 2)
        ));
    }

    private void applyLoadState() {
        usernameInput.setAutofocus(true);
    }

    public static class IDs {
        public static final String PAGE_ID = "registerPage";
        public static final String FORM = "form";
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
