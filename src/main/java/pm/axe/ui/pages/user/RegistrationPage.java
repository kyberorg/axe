package pm.axe.ui.pages.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.shared.Tooltip;
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
import pm.axe.services.user.UserService;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.users.UsernameGenerator;
import pm.axe.users.UsernameValidator;

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
    private static final int PASSWORD_MIN_LEN = 3;
    private static final int PASSWORD_MAX_LEN = 71; //BCrypt limitation

    private final UsernameGenerator usernameGenerator;
    private final UserService userService;

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final FlexLayout userEmailLayout = new FlexLayout();
    private final TextField userEmailInput = new TextField();
    private final Button userEmailInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final FlexLayout usernameLayout = new FlexLayout();
    private final TextField usernameInput = new TextField();
    private final Button usernameInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final Details usernameRequirements = new Details();

    private final FlexLayout passwordLayout = new FlexLayout();
    private final PasswordField passwordInput = new PasswordField();
    private final Button passwordInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final PasswordGenerator passwordGenerator = PasswordGenerator.create();

    private final Span tosNote = createLegalInfo();

    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
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

        setFormFields(userEmailLayout, usernameRequirements, passwordLayout, passwordGenerator);

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
        String input = event.getValue();
        boolean isInputEmpty = StringUtils.isBlank(input);
        if (isInputEmpty) {
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
                getFields().addComponentAtIndex(1, usernameLayout);
                updateLabelForUserEmailInput();
            }
        } else {
            hideUsernameField();
            updateLabelForUserEmailInput();
            //input is username
            OperationResult usernameValidation = UsernameValidator.isValid(input);
            if (usernameValidation.ok()) {
                if (userService.isUserExists(input)) {
                    onInvalidUserEmail("Username already exists");
                }
            } else {
                onInvalidUserEmail("Username doesn't meet requirements");
            }
        }
    }

    private void onUsernameFieldChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        final String username = event.getValue();
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
        String password = event.getValue();
        if (StringUtils.isBlank(password)) return;
        if (password.length() < PASSWORD_MIN_LEN) {
          onInvalidPassword(String.format("Password is very short. Minimum %d symbols", PASSWORD_MIN_LEN));
        } else if (password.length() > PASSWORD_MAX_LEN) {
            onInvalidPassword(String.format("Password is too long. Max %d symbols", PASSWORD_MAX_LEN));
        }
    }

    private void onInvalidUserEmail(final String errorMessage) {
        userEmailInput.setInvalid(true);
        userEmailInput.setErrorMessage(errorMessage);
    }

    private void onInvalidUsername(final String errorMessage) {
        usernameInput.setInvalid(true);
        usernameInput.setErrorMessage(errorMessage);
    }

    public void onInvalidPassword(final String errorMessage) {
        passwordInput.setInvalid(true);
        passwordInput.setErrorMessage(errorMessage);
    }

    private void onRegister(final ClickEvent<Button> event) {
        Notification.show("Not implemented yet");
    }

    private boolean isUsernameLayoutVisible() {
        return getFields().indexOf(usernameLayout) != -1;
    }

    private void hideUsernameField() {
        if (isUsernameLayoutVisible()) {
            getFields().remove(usernameLayout);
        }
    }

    private void updateLabelForUserEmailInput() {
        if (isUsernameLayoutVisible()) {
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
        userEmailInput.setTooltipText("Email stored encrypted. See requirements below.");
        userEmailInput.setClassName("input");

        userEmailInfoButton.setIconAfterText(true);
        userEmailInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        userEmailInfoButton.setClassName("info-button");

        Tooltip userEmailTooltip = userEmailInput.getTooltip().withManual(true);
        userEmailInfoButton.addClickListener(event -> userEmailTooltip.setOpened(!userEmailTooltip.isOpened()));

        userEmailLayout.setAlignItems(Alignment.BASELINE);
        userEmailLayout.add(userEmailInput, userEmailInfoButton);
    }

    private void setupUsernameSection() {
        usernameInput.setLabel("Username");
        usernameInput.setMinLength(USERNAME_MIN_LEN);
        usernameInput.setClearButtonVisible(true);
        usernameInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        usernameInput.addValueChangeListener(this::onUsernameFieldChanged);
        usernameInput.setTooltipText("You can use both as login");
        usernameInput.setClassName("input");

        usernameInfoButton.setIconAfterText(true);
        usernameInfoButton.setClassName("info-button");
        usernameInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Tooltip usernameTooltip = usernameInput.getTooltip().withManual(true);
        usernameInfoButton.addClickListener(event -> usernameTooltip.setOpened(!usernameTooltip.isOpened()));

        usernameLayout.setAlignItems(Alignment.BASELINE);
        usernameLayout.add(usernameInput, usernameInfoButton);
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
        passwordInput.setMinLength(PASSWORD_MIN_LEN);
        passwordInput.setMaxLength(PASSWORD_MAX_LEN);
        passwordInput.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        passwordInput.addValueChangeListener(this::onPasswordFieldChanged);
        passwordInput.setClassName("input");
        passwordInput.setTooltipText(String.format("At least %d symbols. Max %d symbols. " +
                        "Use password generator - make it strong.",
                PASSWORD_MIN_LEN, PASSWORD_MAX_LEN));

        passwordInfoButton.setIconAfterText(true);
        passwordInfoButton.setClassName("info-button");

        passwordInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Tooltip passwordTooltip = passwordInput.getTooltip().withManual(true);
        passwordInfoButton.addClickListener(event -> passwordTooltip.setOpened(!passwordTooltip.isOpened()));

        passwordLayout.add(passwordInput, passwordInfoButton);
        passwordLayout.setAlignItems(Alignment.BASELINE);
    }

    private void setupPasswordGeneratorSection() {
        passwordGenerator.setCopyTarget(passwordInput);
        passwordGenerator.setOpened(false);
    }

    private Span createLegalInfo() {
        Span tosStart = new Span("By signing up, you accept our ");
        Anchor linkToTerms = new Anchor();
        linkToTerms.setHref(Endpoint.UI.TOS_PAGE);
        linkToTerms.setText("Terms of Service");

        Span tosEnd = new Span(".");

        return new Span(tosStart, linkToTerms, tosEnd);
    }
}
