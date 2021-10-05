package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.components.ConfirmationMethodsLayout;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;

import java.util.List;
import java.util.stream.Stream;

@SpringComponent
@UIScope
@CssImport(value = "./css/registration_view.css")
@CssImport(value = "./css/registration_view_form.css", themeFor = "vaadin-form-item")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationView extends YalseeFormLayout {
    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final VerticalLayout usernameSection = new VerticalLayout();
    private final FormLayout usernameFields = new FormLayout();
    private final TextField usernameInput = new TextField();

    private final VerticalLayout confirmationMethodSection = new VerticalLayout();
    private final ConfirmationMethodsLayout confirmationMethodFields = new ConfirmationMethodsLayout();
    private final EmailField emailInput = new EmailField();
    private final TextField telegramInput = new TextField();
    private final Checkbox sameAsUsername = new Checkbox();

    private final FormLayout passwordFields = new FormLayout();
    private final VerticalLayout passwordSection = new VerticalLayout();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField repeatPasswordField = new PasswordField();

    private final Span legalInformationText = new Span();
    private final Anchor linkToTerms = new Anchor();
    private final Span legalInformationEnd = new Span(".");


    public RegistrationView() {
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

        setAdditionalInfo("Leave both password fields empty, " +
                "if you want to receive one time codes every time you log in.");

        setSubmitButtonText("Sign up");
    }

    private List<Component> prepareFormFields() {
        usernameInput.setId(IDs.USERNAME_INPUT);
        usernameFields.addFormItem(usernameInput, "Username");
        usernameSection.add(usernameFields);

        Label confirmationMethodLabel = new Label("Confirmation method");

        emailInput.setId(IDs.EMAIL_INPUT);
        emailInput.setClearButtonVisible(true);

        telegramInput.setId(IDs.TELEGRAM_INPUT);

        sameAsUsername.setLabel("Same as username");
        sameAsUsername.setId(IDs.SAME_AS_USERNAME_CHECKBOX);

        confirmationMethodFields.addItemWithLabel("E-mail", emailInput);
        confirmationMethodFields.addItemWithLabel("Telegram", telegramInput, sameAsUsername);

        confirmationMethodSection.add(confirmationMethodLabel, confirmationMethodFields);

        Label passwordSectionLabel = new Label("Password (optional)");
        passwordField.setId(IDs.PASSWORD_INPUT);
        repeatPasswordField.setId(IDs.REPEAT_PASSWORD_INPUT);

        passwordFields.addFormItem(passwordField, "Password");
        passwordFields.addFormItem(repeatPasswordField, "Same Password");

        passwordSection.add(passwordSectionLabel, passwordFields);

        return List.of(usernameSection, confirmationMethodSection, passwordSection);
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
        Stream<FormLayout> forms = Stream.of(usernameFields, confirmationMethodFields.getContent(), passwordFields);
        forms.forEach(form -> form.setResponsiveSteps(
                new FormLayout.ResponsiveStep(START_POINT, 1),
                new FormLayout.ResponsiveStep(BREAKPOINT, 2)
        ));

        confirmationMethodSection.setClassName("compact-section");
        passwordSection.setClassName("compact-section");
    }

    private void applyLoadState() {
        usernameInput.setAutofocus(true);
    }

    public static class IDs {
        public static final String PAGE_ID = "registerPage";
        public static final String FORM_TITLE = "formTitle";
        public static final String SUBTITLE_TEXT = "subtitleText";
        public static final String SUBTITLE_LINK = "subtitleLink";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String EMAIL_INPUT = "emailInput";
        public static final String TELEGRAM_INPUT = "telegramInput";
        public static final String SAME_AS_USERNAME_CHECKBOX = "sameAsUsernameCheckbox";
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REPEAT_PASSWORD_INPUT = "repeatPasswordInput";
        public static final String LEGAL_INFO_TEXT = "legalInfoText";
        public static final String LEGAL_INFO_TERMS_LINK = "termsLink";
        public static final String LEGAL_INFO_END = "legalInfoEnd";
        public static final String SUBMIT_BUTTON = "submitButton";
    }
}
