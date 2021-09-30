package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
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
@Route(value = Endpoint.UI.REGISTRATION_PAGE + "bkup", layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationViewBkup extends YalseeLayout {

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

    private final Span legalInformationSection = new Span();
    private final Span legalInformationText = new Span();
    private final Anchor linkToTerms = new Anchor();
    private final Span legalInformationEnd = new Span(".");

    private final Span additionalInformation = new Span();

    private final Hr separator = new Hr();

    private final Button submitButton = new Button();

    private final static String START_POINT = "1px";
    private final static String BREAKPOINT = "646px";


    public RegistrationViewBkup() {
        setId(IDs.PAGE_ID);

        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        form.setId(IDs.FORM);

        formTitle = new H2("Become Yalsee User");
        formTitle.setId(IDs.FORM_TITLE);

        usernameInput = new TextField();
        usernameInput.setId(IDs.USERNAME_INPUT);

        usernameFields = new FormLayout();
        usernameFields.addFormItem(usernameInput, "Username");

        usernameSection = new VerticalLayout(usernameFields);

        Label confirmationMethodLabel = new Label("Confirmation method");

        emailInput = new EmailField();
        emailInput.setId(IDs.EMAIL_INPUT);
        emailInput.setClearButtonVisible(true);

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

        legalInformationText.setId(IDs.LEGAL_INFO_TEXT);
        legalInformationText.setText("By registering you accept our ");
        //TODO correct location when ready
        linkToTerms.setId(IDs.LEGAL_INFO_TERMS_LINK);
        linkToTerms.setHref("/appInfo");
        linkToTerms.setText("Terms of Service");

        legalInformationEnd.setId(IDs.LEGAL_INFO_END);

        legalInformationSection.setId(IDs.LEGAL_INFO_SECTION);
        legalInformationSection.add(legalInformationText, linkToTerms, legalInformationEnd);

        additionalInformation.setId(IDs.ADDITIONAL_INFORMATION);
        additionalInformation.setText("Leave passwords empty, if you want to receive one-time codes instead.");

        separator.setId(IDs.SEPARATION_LINE);

        submitButton.setId(IDs.SUBMIT_BUTTON);
        submitButton.setText("Sign Up");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        form.add(formTitle, usernameSection, confirmationMethodSection, passwordSection,
                legalInformationSection, additionalInformation,
                separator, submitButton);
        add(form);
    }

    private void applyStyle() {
        form.setClassName("border");
        form.getStyle().set("background", "white");
        form.setMaxWidth(761, Unit.PIXELS);


        confirmationMethodSection.setClassName("compact-section");
        passwordSection.setClassName("compact-section");

        Stream<FormLayout> forms = Stream.of(usernameFields, confirmationMethodFields.getContent(), passwordFields);
        forms.forEach(form -> form.setResponsiveSteps(
                new FormLayout.ResponsiveStep(START_POINT, 1),
                new FormLayout.ResponsiveStep(BREAKPOINT, 2)
        ));

        submitButton.setWidthFull();
    }

    private void applyLoadState() {
        usernameInput.setAutofocus(true);
    }

    public static class IDs {
        public static final String PAGE_ID = "registerPage";
        public static final String FORM = "form";
        public static final String FORM_TITLE = "formTitle";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String EMAIL_INPUT = "emailInput";
        public static final String TELEGRAM_INPUT = "telegramInput";
        public static final String SAME_AS_USERNAME_CHECKBOX = "sameAsUsernameCheckbox";
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REPEAT_PASSWORD_INPUT = "repeatPasswordInput";
        public static final String LEGAL_INFO_SECTION = "legalInfo";
        public static final String LEGAL_INFO_TEXT = "legalInfoText";
        public static final String LEGAL_INFO_TERMS_LINK = "termsLink";
        public static final String LEGAL_INFO_END = "legalInfoEnd";
        public static final String ADDITIONAL_INFORMATION = "additionInfo";
        public static final String SEPARATION_LINE = "separator";
        public static final String SUBMIT_BUTTON = "submitButton";
    }
}
