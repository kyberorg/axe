package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.internal.LinkServiceInput;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.AuthService;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.utils.ErrorUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static io.kyberorg.yalsee.services.user.UserService.PASSWORD_MIN_LENGTH;

@SpringComponent
@UIScope
@CssImport(value = "./css/registration_view.css")
@CssImport(value = "./css/registration_view_form.css", themeFor = "vaadin-form-item")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationView extends YalseeFormLayout {

    private final UserService userService;
    private final AuthService authService;

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final VerticalLayout usernameSection = new VerticalLayout();
    private final FormLayout usernameFields = new FormLayout();
    private final TextField usernameInput = new TextField();
    private final Span usernameValidation = new Span();
    private Icon usernameValidationIcon = new Icon();
    private final Span usernameValidationText = new Span();

    private final VerticalLayout confirmationMethodSection = new VerticalLayout();
    private final FormLayout confirmationMethodFields = new FormLayout();
    private final EmailField emailInput = new EmailField();
    private final Span emailValidation = new Span();
    private final Span emailValidationFirstText = new Span();
    private final Anchor emailValidationLoginLink = new Anchor();
    private final Span emailValidationSecondText = new Span();

    private final FormLayout passwordFields = new FormLayout();
    private final VerticalLayout passwordSection = new VerticalLayout();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField repeatPasswordField = new PasswordField();

    private final VerticalLayout twoFaSection = new VerticalLayout();
    private final Checkbox twoFaToggle = new Checkbox();

    private final Span legalInformationText = new Span();
    private final Anchor linkToTerms = new Anchor();
    private final Span legalInformationEnd = new Span(".");

    public RegistrationView(final UserService userService, final AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        setId(IDs.PAGE_ID);

        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        setFormTitle("Become Yalsee User");

        subTitleText.setId(IDs.SUBTITLE_TEXT);
        subTitleText.setText("Already have an account? ");

        subTitleLink.setId(IDs.SUBTITLE_LINK);
        subTitleLink.setText("Log in");
        subTitleLink.setHref(Endpoint.UI.LOGIN_PAGE);

        setFormSubTitle(subTitleText, subTitleLink);

        List<Component> formFields = prepareFormFields();
        formFields.forEach(this::addFormFields);

        List<Component> legalInformationFields = createLegalInfo();
        setLegalInfo(legalInformationFields);

        setAdditionalInfo("Enable 2FA protection, " +
                "if you want to receive one time codes every time you log in. " +
                "This can be changed later in your profile.");

        setSubmitButtonText("Sign up");

        getSubmitButton().addClickListener(this::onRegister);
    }

    private List<Component> prepareFormFields() {
        usernameInput.setId(IDs.USERNAME_INPUT);
        usernameInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        usernameInput.addValueChangeListener(this::onUsernameFieldChanged);

        usernameValidationIcon.setId(IDs.USERNAME_VALIDATION_ICON);
        usernameValidationIcon.setVisible(false); //not visible by default
        usernameValidationText.setId(IDs.USERNAME_VALIDATION_TEXT);

        usernameFields.addFormItem(usernameInput, "Username");
        usernameFields.add(usernameValidation);

        usernameValidation.add(usernameValidationIcon, usernameValidationText);
        usernameSection.add(usernameFields);

        Label confirmationMethodLabel = new Label("Confirmation method");

        emailInput.setId(IDs.EMAIL_INPUT);
        emailInput.setClearButtonVisible(true);
        emailInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        emailInput.addValueChangeListener(this::onEmailFieldChanged);

        emailValidation.setId(IDs.EMAIL_VALIDATION);
        emailValidationFirstText.setId(IDs.EMAIL_VALIDATION_TEXT_ONE);
        emailValidationLoginLink.setId(IDs.EMAIL_VALIDATION_LINK);
        emailValidationSecondText.setId(IDs.EMAIL_VALIDATION_TEXT_TWO);
        emailValidation.setVisible(false);

        confirmationMethodFields.addFormItem(emailInput, "E-mail");
        confirmationMethodFields.add(emailValidation);

        confirmationMethodSection.add(confirmationMethodLabel, confirmationMethodFields);

        Label passwordSectionLabel = new Label("Password");
        passwordField.setId(IDs.PASSWORD_INPUT);
        passwordField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        passwordField.addValueChangeListener(this::onPasswordFieldChanged);

        repeatPasswordField.setId(IDs.REPEAT_PASSWORD_INPUT);
        repeatPasswordField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        repeatPasswordField.addValueChangeListener(this::onRepeatPasswordFieldChanged);

        passwordFields.addFormItem(passwordField, "Password");
        passwordFields.addFormItem(repeatPasswordField, "Same Password");

        passwordSection.add(passwordSectionLabel, passwordFields);

        Label twoFaSectionLabel = new Label("Two-Factor Authentication (2FA)");
        twoFaSectionLabel.setId(IDs.TWO_FA_SECTION_LABEL);
        twoFaToggle.setId(IDs.TWO_FA_TOGGLE);
        twoFaToggle.setLabel("Protect my account with additional one time codes");

        twoFaSection.setId(IDs.TWO_FA_SECTION);
        twoFaSection.add(twoFaSectionLabel, twoFaToggle);

        return List.of(usernameSection, confirmationMethodSection, passwordSection, twoFaSection);
    }

    private List<Component> createLegalInfo() {
        legalInformationText.setId(IDs.LEGAL_INFO_TEXT);
      
        legalInformationText.setText("By signing up, you accept our ");
      
        //TODO correct location when ready
        linkToTerms.setId(IDs.LEGAL_INFO_TERMS_LINK);
        linkToTerms.setHref(Endpoint.UI.APP_INFO_PAGE);
        linkToTerms.setText("Terms of Service");

        legalInformationEnd.setId(IDs.LEGAL_INFO_END);

        return List.of(legalInformationText, linkToTerms, legalInformationEnd);
    }

    private void applyStyle() {
        addClassName("limited-by-right-side");
        Stream<FormLayout> forms = Stream.of(usernameFields, confirmationMethodFields, passwordFields);
        forms.forEach(form -> form.setResponsiveSteps(
                new FormLayout.ResponsiveStep(START_POINT, 1),
                new FormLayout.ResponsiveStep(BREAKPOINT, 2)
        ));

        emailValidationFirstText.setClassName("red");
        confirmationMethodSection.setClassName("compact-section");
        passwordSection.setClassName("compact-section");
    }

    private void applyLoadState() {
        usernameInput.setAutofocus(true);
    }

    private void onUsernameFieldChanged(final AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        boolean isAccountAlreadyExists = StringUtils.isNotBlank(event.getValue())
                && userService.isUserExists(event.getValue());
        if (isAccountAlreadyExists) {
            usernameValidationIcon = new Icon(VaadinIcon.CLOSE);
            usernameValidationIcon.setColor("red");
            usernameValidationText.setText(" Username already taken");
            usernameValidationText.setClassName("red");
            usernameInput.setInvalid(true);
        } else {
            usernameValidationIcon = new Icon(VaadinIcon.CHECK);
            usernameValidationIcon.setColor("green");
            usernameValidationText.setText(" Username available");
            usernameValidationText.setClassName("green");
            usernameInput.setInvalid(false);
        }
        usernameValidation.removeAll();
        usernameValidation.add(usernameValidationIcon, usernameValidationText);
        usernameValidationIcon.setId(IDs.USERNAME_VALIDATION_ICON);
        usernameValidationIcon.setVisible(true);
    }

    private void onEmailFieldChanged(final AbstractField.ComponentValueChangeEvent<EmailField, String> event) {
        emailValidation.removeAll();
        emailValidation.setVisible(true);
        if (event.getHasValue().isEmpty()) {
            return;
        }

        boolean isEmailInvalid = event.getSource().isInvalid();
        if (isEmailInvalid) {
            emailValidationFirstText.setText("Please use valid email address.");
            emailValidationFirstText.setClassName("red");
            emailValidation.add(emailValidationFirstText);
            emailValidation.setVisible(true);
            return;
        }

        boolean isEmailAlreadyExists = authService.isEmailAlreadyUsed(event.getValue());
        if (isEmailAlreadyExists) {
            emailValidationFirstText.setText("This e-mail already exists. ");
            emailValidationFirstText.setClassName("red");
            emailValidationLoginLink.setHref(Endpoint.UI.LOGIN_PAGE);
            emailValidationLoginLink.setText("Login");
            emailValidationSecondText.setText(" here. You can use e-mail as username as well.");
            emailValidation.add(emailValidationFirstText, emailValidationLoginLink, emailValidationSecondText);
            emailValidation.setVisible(true);
            emailInput.setInvalid(true);
        } else {
            //remove elements from validation if any and hide it
            emailValidation.setVisible(false);
        }
    }

    private void onPasswordFieldChanged(AbstractField.ComponentValueChangeEvent<PasswordField, String> event) {
        boolean meetsLengthRequirements = StringUtils.isNotBlank(event.getSource().getValue()) &&
                event.getSource().getValue().length() >= PASSWORD_MIN_LENGTH;
        if (meetsLengthRequirements) {
            passwordField.setInvalid(false);
        } else {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage("Minimum " + PASSWORD_MIN_LENGTH + " letters");
        }
    }

    private void onRepeatPasswordFieldChanged(AbstractField.ComponentValueChangeEvent<PasswordField, String> event) {
        String password = passwordField.getValue();
        String repeatPassword = event.getSource().getValue();

        boolean passwordsAreEquals = StringUtils.isNotBlank(repeatPassword) && repeatPassword.equals(password);
        repeatPasswordField.setInvalid(!passwordsAreEquals);
        repeatPasswordField.setErrorMessage("Passwords are different");
    }

    /**
     * On Register Button clicked.
     *
     * @param clickEvent event
     * @see io.kyberorg.yalsee.services.LinkService#createLink(LinkServiceInput)
     */
    private void onRegister(final ClickEvent<Button> clickEvent) {
        String username = usernameInput.getValue();
        String email = emailInput.getValue();
        String password = passwordField.getValue();
        String repeatPassword = repeatPasswordField.getValue();

        if (StringUtils.isBlank(repeatPassword)) {
            ErrorUtils.showError("Password confirmation cannot be empty");
            return;
        }
        if (!repeatPassword.equals(password)) {
            ErrorUtils.showError("Password and confirmation are different");
            return;
        }

        OperationResult userParamsValidationResult = userService.validateParams(username, password);
        if (userParamsValidationResult.ok()) {
            OperationResult emailValidationResult = authService.validateEmail(email);
            if (emailValidationResult.notOk()) {
                ErrorUtils.showError(emailValidationResult.getMessage());
                return;
            }
        } else {
            ErrorUtils.showError(userParamsValidationResult.getMessage());
            return;
        }
        //params are clean
        //OperationResult userCreateResult = userService.createUser(username, password);
    }

    public static class IDs {
        public static final String PAGE_ID = "registerPage";
        public static final String FORM_TITLE = "formTitle";
        public static final String SUBTITLE_TEXT = "subtitleText";
        public static final String SUBTITLE_LINK = "subtitleLink";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String EMAIL_INPUT = "emailInput";
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REPEAT_PASSWORD_INPUT = "repeatPasswordInput";
        public static final String LEGAL_INFO_TEXT = "legalInfoText";
        public static final String LEGAL_INFO_TERMS_LINK = "termsLink";
        public static final String LEGAL_INFO_END = "legalInfoEnd";
        public static final String SUBMIT_BUTTON = "submitButton";
        public static final String USERNAME_VALIDATION_ICON = "usernameValidationIcon";
        public static final String USERNAME_VALIDATION_TEXT = "usernameValidationText";
        public static final String EMAIL_VALIDATION = "emailValidation";
        public static final String EMAIL_VALIDATION_TEXT_ONE = "emailValidationTextOne";
        public static final String EMAIL_VALIDATION_LINK = "emailValidationLink";
        public static final String EMAIL_VALIDATION_TEXT_TWO = "emailValidationTextTwo";
        public static final String TWO_FA_SECTION = "tfaSection";
        public static final String TWO_FA_SECTION_LABEL = "tfaSectionLabel";
        public static final String TWO_FA_TOGGLE = "tfaToggle";
    }
}
