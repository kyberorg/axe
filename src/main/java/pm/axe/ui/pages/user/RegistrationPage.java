package pm.axe.ui.pages.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import pm.axe.Endpoint;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserService;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.users.AccountType;
import pm.axe.users.PasswordValidator;
import pm.axe.users.UsernameGenerator;
import pm.axe.users.UsernameValidator;
import pm.axe.utils.AxeSessionUtils;
import pm.axe.utils.FieldsValidationUtils;

import java.util.Objects;
import java.util.stream.Stream;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/registration_page.css")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Registration - Axe.pm")
public class RegistrationPage extends AxeFormLayout implements BeforeEnterObserver {
    private static final String USERNAME_EMAIL_LABEL = "Username/Email";
    private static final String JUST_EMAIL_LABEL = "Email";
    private static final int USERNAME_MIN_LEN = 2;

    private final UsernameGenerator usernameGenerator;
    private final UserService userService;
    private final AccountService accountService;
    private final AxeSessionUtils axeSessionUtils;
    private final FieldsValidationUtils fieldsValidationUtils;

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();
    private final TextField userEmailInput = new TextField();
    private final TextField usernameInput = new TextField();
    private final Details usernameRequirements = new Details();
    private final PasswordField passwordInput = new PasswordField();
    private final PasswordGenerator passwordGenerator = PasswordGenerator.create();
    private final VerticalLayout tosNote = createLegalInfo();

    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(BeforeEnterEvent enterEvent) {
        if (enterEvent.isRefreshEvent()) return;
        //redirect to landing page if user already in
        if (Objects.nonNull(axeSessionUtils.boundUserIfAny())) {
            enterEvent.forwardTo(axeSessionUtils.getLandingPage().getPath());
            return;
        }
        //init once
        if (pageAlreadyInitialized) {
            cleanInputs();
        } else {
            pageInit();
            pageAlreadyInitialized = true;
        }
    }
    private void pageInit() {
        setCompactMode();
        setFormTitle("Become Axe User");

        subTitleText.setText("Already have an account? ");

        subTitleLink.setText("Log in");
        subTitleLink.setHref(Endpoint.UI.LOGIN_PAGE);

        setFormSubTitle(subTitleText, subTitleLink);

        setupUserEmailSection();
        setupUsernameSection();
        setupUserRequirementsSection();
        setupPasswordSection();
        setupPasswordGeneratorSection();

        setFormFields(userEmailInput, usernameRequirements, passwordInput, passwordGenerator);

        setComponentsAfterFields(tosNote);
        setSubmitButtonText("Sign up");

        getSubmitButton().addClickListener(this::onRegister);
    }

    private void cleanInputs() {
        userEmailInput.clear();
        passwordInput.clear();
        usernameInput.clear();
    }

    private void onUserEmailFieldChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        String input = event.getValue().trim();
        boolean isInputEmpty = StringUtils.isBlank(input);
        if (isInputEmpty) {
            userEmailInput.setInvalid(false);
            hideUsernameField();
            updateLabelForUserEmailInput();
            return;
        }
        boolean isEmail = EmailValidator.getInstance().isValid(input);
        if (isEmail) {
            //input is email
            OperationResult generationResult = usernameGenerator.generateFromEmail(input);
            if (generationResult.ok()) {
                usernameInput.setValue(generationResult.getStringPayload());
                //paste username fields
                getFields().addComponentAtIndex(1, usernameInput);
                updateLabelForUserEmailInput();
                //check if it exists
                boolean alreadyExists = accountService.isAccountAlreadyExists(input, AccountType.EMAIL);
                if (alreadyExists) {
                    onInvalidUserEmail("Email already taken");
                }
            }
        } else {
            hideUsernameField();
            updateLabelForUserEmailInput();
            //input is username
            OperationResult usernameValidation = UsernameValidator.isValid(input);
            if (usernameValidation.ok()) {
                if (userService.isUserExists(input)) {
                    onInvalidUserEmail("Username already taken");
                }
            } else {
                onInvalidUserEmail("Username doesn't meet requirements");
            }
        }
    }

    private void onUsernameFieldChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        final String username = event.getValue().trim();
        if (StringUtils.isBlank(username)) return;
        OperationResult usernameValidation = UsernameValidator.isValid(username);
        if (usernameValidation.ok()) {
            if (userService.isUserExists(username)) {
                onInvalidUsername("Username already exists");
            }
        } else {
            onInvalidUsername("Username doesn't meet requirements");
        }
    }

    private void onPasswordFieldChanged(AbstractField.ComponentValueChangeEvent<PasswordField, String> event) {
        boolean isPasswordValid = !fieldsValidationUtils.isPasswordInvalid(passwordInput);
        if (isPasswordValid) {
            passwordInput.setInvalid(false);
            passwordInput.setErrorMessage("");
        }

    }

    private void onInvalidUserEmail(final String errorMessage) {
        userEmailInput.setInvalid(true);
        userEmailInput.setErrorMessage(errorMessage);
        userEmailInput.focus();
    }

    private void onInvalidUsername(final String errorMessage) {
        usernameInput.setInvalid(true);
        usernameInput.setErrorMessage(errorMessage);
        usernameInput.focus();
    }


    private void onRegister(final ClickEvent<Button> event) {
        Notification.show("Not implemented yet");
    }

    private boolean isUsernameInputVisible() {
        return getFields().indexOf(usernameInput) != -1;
    }

    private void hideUsernameField() {
        if (isUsernameInputVisible()) {
            getFields().remove(usernameInput);
        }
    }

    private void updateLabelForUserEmailInput() {
        if (isUsernameInputVisible()) {
            userEmailInput.setLabel(JUST_EMAIL_LABEL);
        } else {
            userEmailInput.setLabel(USERNAME_EMAIL_LABEL);
        }
    }

    private void setupUserEmailSection() {
        userEmailInput.setLabel(USERNAME_EMAIL_LABEL);
        userEmailInput.setRequired(true);
        userEmailInput.setMinLength(USERNAME_MIN_LEN);
        userEmailInput.setClearButtonVisible(true);
        userEmailInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        userEmailInput.addValueChangeListener(this::onUserEmailFieldChanged);
        userEmailInput.setHelperText("Valid email address or avaialble username");
        userEmailInput.setClassName("input");
    }

    private void setupUsernameSection() {
        usernameInput.setLabel("Username");
        usernameInput.setMinLength(USERNAME_MIN_LEN);
        usernameInput.setClearButtonVisible(true);
        usernameInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        usernameInput.addValueChangeListener(this::onUsernameFieldChanged);
        usernameInput.setHelperText("You can use both as login");
        usernameInput.setClassName("input");
    }

    private void setupUserRequirementsSection() {
        usernameRequirements.setSummaryText("Username requirements");
        Span span = new Span("Username should be");

        UnorderedList requirements = new UnorderedList();
        requirements.removeAll();
        Stream.of("The number of characters must be between 2 and 20.",
                        "Alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.",
                        "Also allowed of the dot (.), underscore (_), and hyphen (-).",
                        "The dot (.), underscore (_), or hyphen (-) must not be the first or last character.",
                        "The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., name..surname.")
                .forEach(requirement -> requirements.add(new ListItem(requirement)));

        usernameRequirements.addContent(span, requirements);
        usernameRequirements.setOpened(false);
    }

    private void setupPasswordSection() {
        passwordInput.setLabel("Password");
        passwordInput.setRequired(true);
        passwordInput.setMinLength(PasswordValidator.PASSWORD_MIN_LENGTH);
        passwordInput.setMaxLength(PasswordValidator.PASSWORD_MAX_LENGTH);
        passwordInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        passwordInput.addValueChangeListener(this::onPasswordFieldChanged);
        passwordInput.setClassName("input");
        passwordInput.setHelperText(String.format("Should be %d-%d symbols long. " +
                "Tip: Use password generator - make it strong.",
                PasswordValidator.PASSWORD_MIN_LENGTH, PasswordValidator.PASSWORD_MAX_LENGTH));
    }

    private void setupPasswordGeneratorSection() {
        passwordGenerator.setCopyTarget(passwordInput);
        passwordGenerator.setOpened(false);
    }

    private VerticalLayout createLegalInfo() {
        Span encryptedSpan = new Span("Data stored encrypted.");
        Span tosStart = new Span("By signing up, you accept our ");
        Anchor linkToTerms = new Anchor();
        linkToTerms.setHref(Endpoint.UI.TOS_PAGE);
        linkToTerms.setText("Terms of Service");

        Span tosEnd = new Span(".");

        Span tosSpan = new Span(tosStart, linkToTerms, tosEnd);
        VerticalLayout legalInfoLayout = new VerticalLayout(encryptedSpan, tosSpan);
        legalInfoLayout.setPadding(false);
        legalInfoLayout.setSpacing(false);
        return legalInfoLayout;
    }
}
