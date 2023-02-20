package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import pm.axe.db.models.Account;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.mail.EmailConfirmationStatus;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.services.user.UserOperationsService;
import pm.axe.services.user.UserService;
import pm.axe.ui.elements.*;
import pm.axe.users.AccountType;
import pm.axe.users.UsernameValidator;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ErrorUtils;
import pm.axe.utils.VaadinUtils;

import java.util.Optional;
import java.util.stream.Stream;

import static pm.axe.utils.VaadinUtils.onInvalidInput;

@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@SpringComponent
@UIScope
public class ProfileTab extends VerticalLayout implements HasTabInit {
    private final AccountService accountService;
    private final TokenService tokenService;
    private final UserService userService;
    private final UserOperationsService userOpsService;
    private User user;

    @SuppressWarnings("FieldCanBeLocal") //will be re-drawn if event received
    private Section accountsSection;

    private final TextField usernameInput = new TextField();
    private final UsernameRequirements usernameRequirements = UsernameRequirements.create();
    private final Button editUsernameButton = new Button();
    private final Button saveUsernameButton = new Button();
    private final HorizontalLayout usernameLayout = new HorizontalLayout();

    private final ConfirmedEmailField emailInput = new ConfirmedEmailField();
    private final Button editEmailButton = new Button();
    private final Button saveEmailButton = new Button();
    private final HorizontalLayout emailLayout = new HorizontalLayout();

    private Details emailUsageDetails;

    private final DeleteConfirmationDialog emailDeleteConfirmationDialog = DeleteConfirmationDialog.create();

    @Override
    public void tabInit(final User user) {
        this.user = user;

        accountsSection = createAccountSection();
        add(accountsSection);

        usernameRequirements.hide();
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        if (StringUtils.isBlank(emailInput.getValue())) {
            emailInput.focus();
            if (emailUsageDetails != null) {
                emailUsageDetails.setOpened(true);
            }
        }
    }

    private Section createAccountSection() {
        HorizontalLayout usernameLayout = createUsernameLayout();
        HorizontalLayout emailLayout = createEmailLayout();

        emailUsageDetails = createEmailUsageDetails();
        HorizontalLayout telegramLayout = createTelegramLayout();

        Stream.of(usernameLayout, emailLayout, emailUsageDetails, telegramLayout).forEach(VaadinUtils::setCentered);

        Section section = new Section("Accounts");
        section.setContent(usernameLayout, usernameRequirements, emailLayout, emailUsageDetails, telegramLayout);
        section.setCentered();
        return section;
    }

    private HorizontalLayout createUsernameLayout() {
        usernameInput.setLabel("Username");
        usernameInput.setValue(user.getUsername());
        usernameInput.setWidthFull();
        usernameInput.addValueChangeListener(this::onUsernameChanged);
        usernameInput.setClearButtonVisible(true);
        usernameInput.setReadOnly(true);

        editUsernameButton.setText("Edit");
        editUsernameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveUsernameButton.setText("Save");
        saveUsernameButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        usernameLayout.add(usernameInput, editUsernameButton);
        VaadinUtils.fitLayoutInWindow(usernameLayout);
        VaadinUtils.setSmallSpacing(usernameLayout);

        editUsernameButton.addClickListener(this::onEditUsername);
        saveUsernameButton.addClickListener(this::onSaveUsername);
        return usernameLayout;
    }

    private HorizontalLayout createEmailLayout() {
        emailInput.setLabel("E-mail");
        emailInput.setClearButtonVisible(true);
        emailInput.setReadOnly(true);
        emailInput.setWidthFull();
        emailInput.addValueChangeListener(this::onEmailChanged);

        Optional<String> currentEmail = getCurrentEmail();
        currentEmail.ifPresent(emailInput::setValue);
        currentEmail.ifPresent(e -> setConfirmationStatus());

        editEmailButton.setText("Edit");
        editEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveEmailButton.setText("Save");
        saveEmailButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        emailDeleteConfirmationDialog.setDeleteButtonAction(this::deleteEmail);

        if (currentEmail.isPresent()) {
            emailLayout.add(emailInput, editEmailButton);
        } else {
            emailInput.setReadOnly(false);
            emailLayout.add(emailInput, saveEmailButton);
        }

        VaadinUtils.fitLayoutInWindow(emailLayout);
        VaadinUtils.setSmallSpacing(emailLayout);

        editEmailButton.addClickListener(this::onEditEmail);
        saveEmailButton.addClickListener(this::onSaveEmail);
        return emailLayout;
    }

    private HorizontalLayout createTelegramLayout() {
        HorizontalLayout telegramLayout = new HorizontalLayout();
        telegramLayout.setAlignItems(Alignment.BASELINE);
        VaadinUtils.fitLayoutInWindow(telegramLayout);
        VaadinUtils.setSmallSpacing(telegramLayout);

        Optional<Account> telegramAccount = getTelegramAccount();
        if (telegramAccount.isPresent()) {
            Optional<String> telegramUsername = accountService.decryptAccountName(telegramAccount.get());
            if (telegramUsername.isPresent()) {
                TextField telegramField = new TextField("Telegram");
                telegramField.setReadOnly(true);
                telegramField.setPrefixComponent(VaadinIcon.AT.create());
                telegramField.setWidthFull();
                telegramField.setValue(telegramUsername.get());
                Button unlinkButton = new Button("Unlink");
                unlinkButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                unlinkButton.addClickListener(this::onTelegramUnlink);
                telegramLayout.add(telegramField, unlinkButton);
            } else {
                telegramLayout.setVisible(false);
            }
        } else {
            Optional<Token> tgToken = getTelegramToken();
            if (tgToken.isPresent()) {
                Details telegramDetails = new Details("Link Telegram Account");
                telegramDetails.setClassName("telegram-details");
                telegramDetails.setOpened(true);
                TelegramSpan telegramSpan = TelegramSpan.create(tgToken.get());
                telegramDetails.setContent(telegramSpan);
                telegramLayout.add(telegramDetails);
            } else {
                telegramLayout.setVisible(false);
            }
        }
        return telegramLayout;
    }

    private Details createEmailUsageDetails() {
        Details howEmailUsedDetails = new Details("What will be sent to email?");

        UnorderedList usageCases = new UnorderedList();
        usageCases.removeAll();
        Stream.of("Account Recovery",
                "Password Reset",
                "Two-Factor Authentication (2FA)")
                .forEach(usageCase -> usageCases.add(new ListItem(usageCase)));

        howEmailUsedDetails.setOpened(false);
        howEmailUsedDetails.setContent(usageCases);
        return howEmailUsedDetails;
    }

    private void onUsernameChanged(final AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        usernameRequirements.hide();
        String input = event.getValue().trim();
        boolean isInputEmpty = StringUtils.isBlank(input);
        if (isInputEmpty) {
            usernameInput.setInvalid(false);
            usernameRequirements.hide();
            return;
        }

        final boolean isSameAsCurrent = user.getUsername().equals(input);
        if (isSameAsCurrent) {
            return;
        }

        final boolean isUsernameValid = !isUsernameInvalid(usernameInput);
        if (isUsernameValid) {
            usernameInput.setInvalid(false);
            usernameInput.setErrorMessage("");
        }
    }

    private void onEditUsername(final ClickEvent<Button> event) {
        usernameInput.setReadOnly(false);
        usernameLayout.replace(editUsernameButton, saveUsernameButton);
    }

    private void onSaveUsername(final ClickEvent<Button> event) {
        if (StringUtils.isBlank(usernameInput.getValue().trim())) {
            usernameInput.setInvalid(true);
            usernameInput.setErrorMessage("Username cannot be empty");
            return;
        }
        //is same as current ?
        boolean isSameAsCurrent = user.getUsername().equals(usernameInput.getValue().trim());
        if (isSameAsCurrent) {
            usernameInput.setReadOnly(true);
            usernameLayout.replace(saveUsernameButton, editUsernameButton);
            return;
        }

        //changed -> validate
        if (isUsernameInvalid(usernameInput)) {
            return;
        }

        //clean -> save
        OperationResult usernameUpdate = userService.updateUsername(user, usernameInput.getValue());
        if (usernameUpdate.ok()) {
            AppUtils.showSuccessNotification("Username is updated");
        } else {
            ErrorUtils.showErrorNotification("Failed to update Username. Server error");
        }
        usernameInput.setReadOnly(true);
        usernameLayout.replace(saveUsernameButton, editUsernameButton);
    }

    private void onEmailChanged(final AbstractField.ComponentValueChangeEvent<EmailField, String> event) {
        final String email = event.getValue().trim();
        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (StringUtils.isBlank(email)) {
            emailInput.setInvalid(false);
            emailInput.setErrorMessage("");
            return;
        }

        Optional<String> currentEmail = getCurrentEmail();
        if (currentEmail.isPresent()) {
            boolean isSameAsCurrent = currentEmail.get().equals(email);
            if (isSameAsCurrent) {
                return;
            }
        }

        if (isValidEmail) {
            boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
            if (emailExists) {
                onInvalidInput(emailInput, "Email already taken");
            }
        } else {
            //not valid email
            onInvalidInput(emailInput, "Should be valid email");
        }
    }

    private void onEditEmail(final ClickEvent<Button> event) {
        emailInput.setReadOnly(false);
        emailInput.setSuffixComponent(new Div());
        emailLayout.replace(editEmailButton, saveEmailButton);
    }

    private void onSaveEmail(final ClickEvent<Button> event) {
        final String email = emailInput.getValue().trim();
        Optional<String> currentEmail = getCurrentEmail();
        if (StringUtils.isBlank(email) && currentEmail.isPresent()) {
            emailDeleteConfirmationDialog.show();

            emailInput.setReadOnly(true);
            emailLayout.replace(saveEmailButton, editEmailButton);
            return;
        } else if (StringUtils.isBlank(email)) {
            emailInput.setInvalid(false);
            emailInput.setErrorMessage("");

            emailInput.setReadOnly(true);
            emailLayout.replace(saveEmailButton, editEmailButton);
            return;
        }

        //is same as current ?
        if (currentEmail.isPresent()) {
            boolean isSameAsCurrent = currentEmail.get().equals(email);
            if (isSameAsCurrent) {
                emailInput.setReadOnly(true);
                emailLayout.replace(saveEmailButton, editEmailButton);

                setConfirmationStatus();
                return;
            }
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

        OperationResult emailUpdateResult = userOpsService.updateEmailAccount(user, email);
        if (emailUpdateResult.notOk()) {
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
            if (currentEmail.isPresent()) {
                emailInput.setValue(currentEmail.get());
            } else {
                emailInput.setValue("");
            }
            ErrorUtils.showErrorNotification("Failed to update email. Server error");
            return;
        }

        //creating confirmation token
        Account userAccount = emailUpdateResult.getPayload(Account.class);
        Optional<Token> confirmationToken = userOpsService.createConfirmationToken(userAccount);
        if (confirmationToken.isEmpty()) {
            emailInput.setSuffixComponent(VaadinIcon.EXCLAMATION_CIRCLE.create());
            ErrorUtils.showErrorNotification("Failed to send confirmation letter. Please try again later.");
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
            return;
        }

        //sending confirmation letter
        OperationResult sendConfirmationLetterResult =
                userOpsService.sendConfirmationLetter(confirmationToken.get(), email, userAccount);
        if (sendConfirmationLetterResult.ok()) {
            emailInput.setSuffixComponent(VaadinIcon.ELLIPSIS_CIRCLE.create());
            AppUtils.showSuccessNotification("Send confirmation to new email. Please check your inbox.");
        } else {
            emailInput.setSuffixComponent(VaadinIcon.EXCLAMATION_CIRCLE.create());
            ErrorUtils.showErrorNotification("Failed to send confirmation letter. Please try again later.");
            currentEmailRecord.ifPresent(accountService::rollbackAccount);
        }

        emailInput.setReadOnly(true);
        emailLayout.replace(saveEmailButton, editEmailButton);
    }

    private void onTelegramUnlink(final ClickEvent<Button> event) {
        AreYouSureDialog dialog = AreYouSureDialog.create("Unlink Telegram Account");
        dialog.setActionButtonText("Unlink");
        dialog.setActionButtonAction(this::unlinkTelegram);
        dialog.show();
    }

    private void unlinkTelegram() {
        AppUtils.showSuccessNotification("Will do once implemented");
    }

    private boolean isUsernameInvalid(final TextField inputField) {
        final String input = inputField.getValue().trim();
        OperationResult usernameValidation = UsernameValidator.isValid(input);
        if (usernameValidation.ok()) {
            if (userService.isUserExists(input)) {
                onInvalidInput(inputField, "Username already taken");
                return true;
            }
        } else {
            onInvalidInput(inputField, "Username doesn't meet requirements");
            usernameRequirements.show();
            return true;
        }
        return false;
    }

    private Optional<String> getCurrentEmail() {
        Optional<Account> emailAccount = accountService.getAccount(user, AccountType.EMAIL);
        if (emailAccount.isEmpty()) return Optional.empty();
        Optional<String> plainTextEmail = accountService.decryptAccountName(emailAccount.get());
        if (plainTextEmail.isEmpty()) return Optional.empty();
        if (StringUtils.isNotBlank(plainTextEmail.get())) {
            return plainTextEmail;
        } else {
            return Optional.empty();
        }
    }

    private boolean isCurrentEmailConfirmed() {
        Optional<Account> emailAccount = accountService.getAccount(user, AccountType.EMAIL);
        return emailAccount.map(Account::isConfirmed).orElse(false);
    }

    private void setConfirmationStatus() {
        boolean hasEmail = getCurrentEmail().isPresent();
        if (hasEmail) {
            EmailConfirmationStatus confirmationStatus = isCurrentEmailConfirmed()
                    ?  EmailConfirmationStatus.CONFIRMED : EmailConfirmationStatus.PENDING;
            emailInput.setStatus(confirmationStatus);
        } else {
            emailInput.setStatus(EmailConfirmationStatus.NONE);
        }
    }

    private Optional<Account> getTelegramAccount() {
        return accountService.getAccount(user, AccountType.TELEGRAM);
    }

    private Optional<Token> getTelegramToken() {
        Optional<Token> telegramToken = tokenService.getTelegramToken(user);
        Token tgToken;
        if (telegramToken.isPresent()) {
            tgToken = telegramToken.get();
        } else {
            OperationResult tokenCreateResult = tokenService.createTelegramConfirmationToken(user);
            if (tokenCreateResult.ok()) {
                tgToken = tokenCreateResult.getPayload(Token.class);
            } else {
                tgToken = null;
            }
        }
        return Optional.ofNullable(tgToken);
    }

    private void deleteEmail() {
        Optional<Account> emailAccount = accountService.getAccount(user, AccountType.EMAIL);
        if (emailAccount.isPresent()) {
            OperationResult result = userOpsService.deleteAccountOnly(emailAccount.get());
            if (result.ok()) {
                AppUtils.showSuccessNotification("Email successfully deleted");
                emailInput.setStatus(EmailConfirmationStatus.NONE);
            } else {
                ErrorUtils.showErrorNotification("Failed to delete email. System error");
                emailInput.setStatus(EmailConfirmationStatus.FAILED);
            }
        }

    }
}
