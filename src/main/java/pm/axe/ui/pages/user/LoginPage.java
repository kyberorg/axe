package pm.axe.ui.pages.user;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
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
import pm.axe.db.models.Account;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.users.AccountType;
import pm.axe.users.LandingPage;
import pm.axe.utils.AppUtils;
import pm.axe.utils.AxeSessionUtils;
import pm.axe.utils.ErrorUtils;
import pm.axe.utils.FieldsValidationUtils;

import java.util.Objects;
import java.util.Optional;

import static pm.axe.utils.VaadinUtils.onInvalidInput;


@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/login_page.css")
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Login Page - Axe.pm")
public class LoginPage extends AxeFormLayout implements BeforeEnterObserver {
    private final AppUtils appUtils;
    private final UserService userService;
    private final AccountService accountService;
    private final AxeSessionUtils axeSessionUtils;
    private final FieldsValidationUtils fieldsValidationUtils;
    private final MainView mainView;

    private final Span subTitleText = new Span();
    private final Span spaceSpan = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox forgotMe = new Checkbox();
    private final Button forgotMeInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final Section forgotPasswordSection = new Section();
    private final Anchor forgotPasswordLink = new Anchor();

    private boolean pageAlreadyInitialized = false;
    @Override
    public void beforeEnter(final BeforeEnterEvent enterEvent) {
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
            init();
            pageAlreadyInitialized = true;
        }
    }

    private void init() {
        setCompactMode();
        getForm().getStyle().set("width", "min(380px, 100%) !important"); //dirty fix for large screens

        setFormTitle("Sign in to Axe");
        subTitleText.setText("New to Axe?");
        spaceSpan.setText(" ");
        subTitleLink.setText("Register here");
        subTitleLink.setHref(Endpoint.UI.REGISTRATION_PAGE);

        setFormSubTitle(subTitleText, spaceSpan, subTitleLink);

        usernameInput.setLabel("Username/Email");
        usernameInput.setClearButtonVisible(true);
        usernameInput.setRequired(true);
        usernameInput.addValueChangeListener(this::onUsernameChanged);

        passwordInput.setLabel("Password");
        passwordInput.setRequired(true);
        passwordInput.addValueChangeListener(this::onPasswordChanged);

        forgotMe.setLabel("Log me out after");
        forgotMe.setTooltipText("If enabled, Axe will log you out once current session is over");
        forgotMeInfoButton.setIconAfterText(true);
        forgotMeInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        forgotMeInfoButton.setClassName("info-button");

        Tooltip forgotMeTooltip = forgotMe.getTooltip().withManual(true);
        forgotMeInfoButton.addClickListener(e -> forgotMeTooltip.setOpened(!forgotMeTooltip.isOpened()));
        FlexLayout forgotMeLayout = new FlexLayout(forgotMe, forgotMeInfoButton);
        forgotMeLayout.setAlignItems(Alignment.BASELINE);

        setFormFields(usernameInput, passwordInput, forgotMeLayout);

        setSubmitButtonText("Jump in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onLogin);

        forgotPasswordLink.setHref(Endpoint.UI.FORGOT_PASSWORD_PAGE);
        forgotPasswordLink.setText("Forgot your password?");
        forgotPasswordLink.setClassName("forgot-password-link");
        forgotPasswordSection.add(forgotPasswordLink);
        forgotPasswordSection.setClassName("forgot-password-section");

        setComponentsAfterSubmitButton(forgotPasswordSection);
    }

    private void onUsernameChanged(final AbstractField.ComponentValueChangeEvent<TextField, String> e) {
        boolean isEmail = EmailValidator.getInstance().isValid(usernameInput.getValue().trim());
        boolean inputInvalid = isEmail
                ? fieldsValidationUtils.isEmailInvalid(usernameInput)
                : fieldsValidationUtils.isUsernameInvalid(usernameInput);


        if (!inputInvalid) {
            usernameInput.setInvalid(false);
            usernameInput.setErrorMessage("");
        }
    }

    private void onPasswordChanged(final AbstractField.ComponentValueChangeEvent<PasswordField, String> e) {
        boolean isPasswordValid = !fieldsValidationUtils.isPasswordInvalid(passwordInput);
        if (isPasswordValid) {
            passwordInput.setInvalid(false);
            passwordInput.setErrorMessage("");
        }
    }

    private void onLogin(final ClickEvent<Button> event) {
        String userOrEmail = usernameInput.getValue().trim();
        boolean isInputEmpty = StringUtils.isBlank(userOrEmail);
        if (isInputEmpty) {
            onInvalidInput(usernameInput, "Please type username or email here");
            return;
        }

        boolean isEmail = EmailValidator.getInstance().isValid(userOrEmail);
        boolean userOrEmailInvalid = isEmail
                ? fieldsValidationUtils.isEmailInvalid(usernameInput)
                : fieldsValidationUtils.isUsernameInvalid(usernameInput);
        if (userOrEmailInvalid) return;
        if (fieldsValidationUtils.isPasswordInvalid(passwordInput)) return;

        //TODO read value of forgotMe and at accordingly
        //FIXME remove after real login progress implemented
        if (appUtils.isDevelopmentModeActivated()) {
            doEasyLogin();
        } else {
            Notification.show("Not implemented yet");
        }
    }

    private void doEasyLogin() {
        AxeSession.getCurrent().ifPresent(axs -> {
            final String input = usernameInput.getValue().trim();
            boolean isInputEmail = EmailValidator.getInstance().isValid(input);
            String username;
            if (isInputEmail) {
                Optional<String> usernameFromEmail = getUsernameByEmail(input);
                if (usernameFromEmail.isPresent()) {
                    username = usernameFromEmail.get();
                } else {
                    ErrorUtils.getErrorNotification("Something went wrong, because try using username instead");
                    return;
                }
            } else {
                username = input;
            }
            Optional<User> user = userService.getUserByUsername(username);
            user.ifPresent(axs::setUser);
            Optional<UserSettings> us = axeSessionUtils.getCurrentUserSettings();
            //dark mode
            if (us.isPresent() && us.get().isDarkMode()) {
                axs.getSettings().setDarkMode(true);
                mainView.applyTheme(true);
            }
            //landing page
            LandingPage landingPage = axeSessionUtils.getLandingPage(); //page after login
            usernameInput.getUI().ifPresent(ui -> ui.navigate(landingPage.getPath()));
        });
    }

    private Optional<String> getUsernameByEmail(final String email) {
        Optional<Account> account = accountService.getAccountByAccountName(email, AccountType.EMAIL);
        return account.map(owner -> owner.getUser().getUsername());
    }

    private void cleanInputs() {
        cleanInput(usernameInput);
        cleanInput(passwordInput);
        cleanInput(forgotMe);
    }

    private void cleanInput(final Component input) {
        if (input instanceof HasValue<?,?>) {
            ((HasValue<?, ?>) input).clear();
        }
        if (input instanceof HasValidation) {
            ((HasValidation) input).setInvalid(false);
            ((HasValidation) input).setErrorMessage("");
        }
    }
}
