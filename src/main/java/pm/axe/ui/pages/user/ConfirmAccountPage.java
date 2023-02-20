package pm.axe.ui.pages.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
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
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.mail.EmailConfirmationStatus;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.services.user.UserOperationsService;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.ConfirmedEmailField;
import pm.axe.ui.elements.Section;
import pm.axe.ui.elements.TelegramSpan;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.users.AccountType;
import pm.axe.utils.AppUtils;
import pm.axe.utils.AxeSessionUtils;
import pm.axe.utils.ErrorUtils;
import pm.axe.utils.VaadinUtils;

import java.util.Objects;
import java.util.Optional;

import static pm.axe.utils.VaadinUtils.onInvalidInput;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@Route(value = Endpoint.UI.CONFIRM_ACCOUNT_PAGE, layout = MainView.class)
@PageTitle("Confirm Account - Axe.pm")
public class ConfirmAccountPage extends AxeCompactLayout implements BeforeEnterObserver {
    private final TokenService tokenService;
    private final AccountService accountService;
    private final UserOperationsService userOperationsService;
    private final AxeSessionUtils axeSessionUtils;
    private boolean pageAlreadyInitialized = false;
    private User user;

    private boolean hasEmail = false;
    private boolean hasConfirmedEmail = false;

    private HorizontalLayout emailLayout;
    private ConfirmedEmailField emailInput;
    private Button submitEmailButton;
    private Span emailSpan;

    private Section telegramSection;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        user = axeSessionUtils.boundUserIfAny();
        if (Objects.isNull(user)) {
            event.forwardTo(LoginPage.class);
            return;
        }
        if (!pageAlreadyInitialized) {
            initPage();
            pageAlreadyInitialized = true;
        }
    }

    private void initPage() {
        hasEmail = accountService.isAccountExist(user, AccountType.EMAIL);

        if (hasEmail) {
            hasConfirmedEmail = accountService.isCurrentEmailConfirmed(user);
        }

        H3 title = new H3("Confirm your Account");

       Component emailSectionContent = hasConfirmedEmail ?
               createAccountConfirmedSection(AccountType.EMAIL) : emailSectionContent();

        Section emailSection = new Section("Using Email");
        emailSection.setCustomContent(emailSectionContent);

        //TG account is already confirmed
        boolean hasConfirmedTelegram = accountService.isAccountExist(user, AccountType.TELEGRAM);

        Component telegramSectionContent = hasConfirmedTelegram ?
                createAccountConfirmedSection(AccountType.TELEGRAM) : telegramSectionContent();

        telegramSection = new Section("Using Telegram");
        telegramSection.setCustomContent(telegramSectionContent);

        add(title, emailSection, telegramSection);
    }

    private Component emailSectionContent() {
        emailInput = new ConfirmedEmailField();
        emailInput.setLabel("Email address here");
        emailInput.setHelperText("Axe will send confirmation letter there");
        emailInput.setClearButtonVisible(true);
        emailInput.addValueChangeListener(this::onEmailChanged);

        Optional<String> currentEmail = accountService.getCurrentEmail(user);
        currentEmail.ifPresent(email -> emailInput.setValue(email));

        submitEmailButton = new Button("Submit");
        submitEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitEmailButton.addClickListener(this::onSubmitEmail);

        emailSpan = new Span();

        Details nothingCame = null;

        if (hasEmail) {
            //user has hasEmail - RO input + span
            emailInput.setReadOnly(true);
            EmailConfirmationStatus status = hasConfirmedEmail ?
                    EmailConfirmationStatus.CONFIRMED : EmailConfirmationStatus.PENDING;
            emailInput.setStatus(status);
            emailSpan.setText(status.getStatusString());
            if (status == EmailConfirmationStatus.CONFIRMED) {
                emailSpan.setClassName("green");
            } else {
                emailInput.setHelperText("Axe sent confirmation letter to given email.");
                nothingCame = getNothingCameDetails();
            }
            emailLayout = new HorizontalLayout(emailInput, emailSpan);
        } else {
            //no email - input + button
            emailLayout = new HorizontalLayout(emailInput, submitEmailButton);
        }

        emailLayout.setWidthFull();
        VaadinUtils.fitLayoutInWindow(emailLayout);
        VaadinUtils.setSmallSpacing(emailLayout);

        if (nothingCame != null) {
            VerticalLayout layout = new VerticalLayout(emailLayout, nothingCame);
            layout.setPadding(false);
            return layout;
        } else {
            return emailLayout;
        }
    }


    private Component telegramSectionContent() {
        Optional<Token> telegramToken = tokenService.getTelegramToken(user);
        Token tgToken;
        if (telegramToken.isPresent()) {
            tgToken = telegramToken.get();
        } else {
            OperationResult tokenCreateResult = tokenService.createTelegramConfirmationToken(user);
            if (tokenCreateResult.ok()) {
                tgToken = tokenCreateResult.getPayload(Token.class);
            } else {
                telegramSection.setVisible(false);
                return new Span("Failed to generate Telegram Token. System error,");
            }
        }
        return TelegramSpan.create(tgToken);
    }

    private Component createAccountConfirmedSection(final AccountType accountType) {
        if (accountType == null) throw new IllegalArgumentException("accountType cannot be null");
        Icon successMark = VaadinIcon.CHECK.create();
        successMark.setColor("green");
        Span text = new Span("You have successfully confirmed your ");
        Span accType = new Span(StringUtils.capitalize(accountType.name().toLowerCase()));
        Span account = new Span(" account");

        Span textSpan = new Span(text, accType, account);
        HorizontalLayout layout = new HorizontalLayout(successMark, textSpan);
        layout.setAlignItems(Alignment.CENTER);
        layout.setMaxHeight("100%");
        return layout;
    }

    private void onEmailChanged(final AbstractField.ComponentValueChangeEvent<EmailField, String> e) {
        if (!e.isFromClient()) return;
        final String email = e.getValue().trim();
        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (StringUtils.isBlank(email)) {
            emailInput.setInvalid(false);
            emailInput.setErrorMessage("");
            if (!submitEmailButton.isEnabled()) {
                submitEmailButton.setEnabled(true);
            }
            return;
        }
        if (isValidEmail) {
            boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
            if (emailExists) {
                onInvalidInput(emailInput, "Email already taken");
                submitEmailButton.setEnabled(false);
            } else {
                emailInput.setInvalid(false);
                emailInput.setErrorMessage("");
                if (!submitEmailButton.isEnabled()) {
                    submitEmailButton.setEnabled(true);
                }
            }
        } else {
            //not valid email
            onInvalidInput(emailInput, "Should be valid email");
            submitEmailButton.setEnabled(false);
        }
    }

    private void onSubmitEmail(final ClickEvent<Button> event) {
        final String email = emailInput.getValue().trim();
        if (StringUtils.isBlank(email)) {
            emailInput.setInvalid(true);
            emailInput.setErrorMessage("Please enter valid email");
            return;
        }
        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (isValidEmail) {
            //email
            boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
            if (emailExists) {
                onInvalidInput(emailInput, "Email already taken");
                return;
            }
        } else {
            //not valid email
            onInvalidInput(emailInput, "Should be valid email address");
            return;
        }

        //clean -> save
        Optional<Account> currentEmailRecord = accountService.getAccount(user, AccountType.EMAIL);
        OperationResult emailUpdateResult = userOperationsService.updateEmailAccount(user, email);
        if (emailUpdateResult.notOk()) {
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
            emailInput.setStatus(EmailConfirmationStatus.FAILED);
            ErrorUtils.showErrorNotification("Failed to update email. Server error");
            return;
        }
        //creating confirmation token
        Account userAccount = emailUpdateResult.getPayload(Account.class);
        Optional<Token> confirmationToken = userOperationsService.createConfirmationToken(userAccount);
        if (confirmationToken.isEmpty()) {
            emailInput.setStatus(EmailConfirmationStatus.FAILED);
            ErrorUtils.showErrorNotification("Failed to send confirmation letter. Please try again later.");
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
            return;
        }
        OperationResult sendConfirmationLetterResult =
                userOperationsService.sendConfirmationLetter(confirmationToken.get(), email, userAccount);
        if (sendConfirmationLetterResult.ok()) {
            emailInput.setStatus(EmailConfirmationStatus.PENDING);
            emailSpan.setText("Sent! Confirmation is pending");
            emailSpan.setClassName("green");
            AppUtils.showSuccessNotification("Send confirmation to new email. Please check your inbox.");
        } else {
            emailInput.setStatus(EmailConfirmationStatus.FAILED);

            emailSpan.setText("Failed to send confirmation letter");
            emailSpan.setClassName("red");
            ErrorUtils.showErrorNotification("Failed to send confirmation letter. Please try again later.");
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
        }
        emailInput.setValue(email);
        emailInput.setReadOnly(true);
        emailLayout.replace(submitEmailButton, emailSpan);
    }

    private Details getNothingCameDetails() {
        Details details = new Details("Didn't receive email?");

        //body
        Span checkFolders = new Span("Please check inbox or spam folders. ");

        Span stillNothing = new Span("Still nothing? ");

        Span goBack = new Span("Go back to ");
        Anchor profilePage = new Anchor(Endpoint.UI.PROFILE_PAGE, "Profile Page");
        Span tryAgain = new Span(" and try again with another email.");

        Span stillNothingSpan = new Span(stillNothing, goBack, profilePage, tryAgain);

        VerticalLayout body = new VerticalLayout(checkFolders, stillNothingSpan);
        body.setPadding(false);
        body.setSpacing(false);

        details.setContent(body);
        return details;
    }
}
