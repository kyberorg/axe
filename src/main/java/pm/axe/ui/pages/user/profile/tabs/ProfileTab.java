package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
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
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.services.user.UserService;
import pm.axe.ui.elements.AreYouSureDialog;
import pm.axe.ui.elements.Section;
import pm.axe.ui.elements.TelegramSpan;
import pm.axe.ui.elements.UsernameRequirements;
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
    private User user;

    @SuppressWarnings("FieldCanBeLocal") //will be re-drawn if event received
    private Section accountsSection;

    private final TextField usernameField = new TextField();
    private final UsernameRequirements usernameRequirements = UsernameRequirements.create();
    private final Button editUsernameButton = new Button();
    private final Button saveUsernameButton = new Button();
    private final HorizontalLayout usernameLayout = new HorizontalLayout();

    private final EmailField emailField = new EmailField();
    private final Button editEmailButton = new Button();
    private final Button saveEmailButton = new Button();
    private final HorizontalLayout emailLayout = new HorizontalLayout();

    @Override
    public void tabInit(final User user) {
        this.user = user;

        accountsSection = createAccountSection();
        add(accountsSection);

        usernameRequirements.hide();
    }

    private Section createAccountSection() {
        HorizontalLayout usernameLayout = createUsernameLayout();
        HorizontalLayout emailLayout = createEmailLayout();
        Details emailUsageDetails = createEmailUsageDetails();
        HorizontalLayout telegramLayout = createTelegramLayout();

        Stream.of(usernameLayout, emailLayout, telegramLayout).forEach(VaadinUtils::setCentered);

        Section section = new Section("Accounts");
        section.setContent(usernameLayout, usernameRequirements, emailLayout, emailUsageDetails, telegramLayout);
        section.setCentered();
        return section;
    }

    private HorizontalLayout createUsernameLayout() {
        usernameField.setLabel("Username");
        usernameField.setValue(user.getUsername());
        usernameField.addValueChangeListener(this::onUsernameChanged);
        usernameField.setClearButtonVisible(true);
        usernameField.setReadOnly(true);

        editUsernameButton.setText("Edit");
        editUsernameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveUsernameButton.setText("Save");
        saveUsernameButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        usernameLayout.add(usernameField, editUsernameButton);
        usernameLayout.addClassName("fit-in-section");
        VaadinUtils.fitLayoutInWindow(usernameLayout);
        VaadinUtils.setSmallSpacing(usernameLayout);

        editUsernameButton.addClickListener(this::onEditUsername);
        saveUsernameButton.addClickListener(this::onSaveUsername);
        return usernameLayout;
    }

    private HorizontalLayout createEmailLayout() {
        emailField.setLabel("E-mail");

        emailField.setClearButtonVisible(true);
        emailField.setReadOnly(true);
        emailField.addValueChangeListener(this::onEmailChanged);

        getCurrentEmail().ifPresent(emailField::setValue);

        editEmailButton.setText("Edit");
        editEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveEmailButton.setText("Save");
        saveEmailButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        emailLayout.add(emailField, editEmailButton);
        emailLayout.addClassName("fit-in-section");
        VaadinUtils.fitLayoutInWindow(emailLayout);
        VaadinUtils.setSmallSpacing(emailLayout);

        editEmailButton.addClickListener(this::onEditEmail);
        saveEmailButton.addClickListener(this::onSaveEmail);
        return emailLayout;
    }

    private HorizontalLayout createTelegramLayout() {
        HorizontalLayout telegramLayout = new HorizontalLayout();
        telegramLayout.setAlignItems(Alignment.BASELINE);
        telegramLayout.addClassName("fit-in-section");
        VaadinUtils.fitLayoutInWindow(telegramLayout);
        VaadinUtils.setSmallSpacing(telegramLayout);

        Optional<Account> telegramAccount = getTelegramAccount();
        if (telegramAccount.isPresent()) {
            Optional<String> telegramUsername = accountService.decryptAccountName(telegramAccount.get());
            if (telegramUsername.isPresent()) {
                TextField telegramField = new TextField("Telegram");
                telegramField.setReadOnly(true);
                telegramField.setPrefixComponent(VaadinIcon.AT.create());
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
            usernameField.setInvalid(false);
            usernameRequirements.hide();
            return;
        }

        final boolean isSameAsCurrent = user.getUsername().equals(input);
        if (isSameAsCurrent) {
            return;
        }

        final boolean isUsernameValid = !isUsernameInvalid(usernameField);
        if (isUsernameValid) {
            usernameField.setInvalid(false);
            usernameField.setErrorMessage("");
        }
    }

    private void onEditUsername(final ClickEvent<Button> event) {
        usernameField.setReadOnly(false);
        usernameLayout.replace(editUsernameButton, saveUsernameButton);
    }

    private void onSaveUsername(final ClickEvent<Button> event) {
        if (StringUtils.isBlank(usernameField.getValue().trim())) {
            usernameField.setInvalid(true);
            usernameField.setErrorMessage("Username cannot be empty");
            return;
        }
        //is same as current ?
        boolean isSameAsCurrent = user.getUsername().equals(usernameField.getValue().trim());
        if (isSameAsCurrent) {
            usernameField.setReadOnly(true);
            usernameLayout.replace(saveUsernameButton, editUsernameButton);
            return;
        }

        //changed -> validate
        if (isUsernameInvalid(usernameField)) {
            return;
        }

        //clean -> save
        OperationResult usernameUpdate = userService.updateUsername(user, usernameField.getValue());
        if (usernameUpdate.ok()) {
            AppUtils.showSuccessNotification("Username is updated");
        } else {
            ErrorUtils.showErrorNotification("Failed to update Username. Server error");
        }
        usernameField.setReadOnly(true);
        usernameLayout.replace(saveUsernameButton, editUsernameButton);
    }

    private void onEmailChanged(final AbstractField.ComponentValueChangeEvent<EmailField, String> event) {
        final String email = event.getValue().trim();
        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (StringUtils.isBlank(email)) {
            usernameField.setInvalid(false);
            usernameField.setErrorMessage("");
            return;
        }

        Optional<String> currentEmail = getCurrentEmail();
        if (currentEmail.isEmpty()) return;
        boolean isSameAsCurrent = currentEmail.get().equals(email);
        if (isSameAsCurrent) {
            return;
        }

        if (isValidEmail) {
            boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
            if (emailExists) {
                onInvalidInput(emailField, "Email already taken");
            }
        } else {
            //not valid email
            onInvalidInput(emailField, "Should be valid email");
        }
    }

    private void onEditEmail(ClickEvent<Button> event) {
        emailField.setReadOnly(false);
        emailLayout.replace(editEmailButton, saveEmailButton);
    }

    private void onSaveEmail(ClickEvent<Button> event) {
        //is same as current ?
        Optional<String> currentEmail = getCurrentEmail();
        if (currentEmail.isEmpty()) return;
        final String email = emailField.getValue().trim();
        boolean isSameAsCurrent = currentEmail.get().equals(email);
        if (isSameAsCurrent) {
            emailField.setReadOnly(true);
            emailLayout.replace(saveEmailButton, editEmailButton);
            return;
        }

        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (isValidEmail) {
            //email
            boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
            if (emailExists) {
                onInvalidInput(emailField, "Email already taken");
                return;
            }
        } else {
            //not valid email
            onInvalidInput(emailField, "Should be valid email");
            return;
        }

        //clean -> save
        OperationResult emailUpdateResult = accountService.updateEmailAccount(user, email);
        if (emailUpdateResult.ok()) {
            AppUtils.showSuccessNotification("Email is updated");
        } else {
            ErrorUtils.showErrorNotification("Failed to update email. Server error");
        }

        emailField.setReadOnly(true);
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
}
