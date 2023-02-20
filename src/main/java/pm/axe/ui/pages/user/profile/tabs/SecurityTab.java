package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Endpoint;
import pm.axe.db.models.Account;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.internal.HasTabInit;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserService;
import pm.axe.services.user.UserSettingsService;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.ui.elements.Section;
import pm.axe.users.AccountType;
import pm.axe.utils.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static pm.axe.utils.VaadinUtils.onInvalidInput;

@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@SpringComponent
@UIScope
public class SecurityTab extends VerticalLayout implements HasTabInit {
    private final AccountService accountService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final AxeSessionUtils axeSessionUtils;
    private final FieldsValidationUtils fieldsValidationUtils;

    private List<Account> confirmedAccounts;

    private Section changePasswordSection;
    private Section resetPasswordChannelSection;
    private Section tfaSection;

    private PasswordField oldPasswordInput;
    private PasswordField newPasswordInput;

    private Select<String> resetPasswordSelect;

    private Checkbox tfaBox;
    private TextField tfaField;
    private Select<String> tfaChannelSelect;
    private User user;

    private boolean isClientChange;

    @Override
    public void tabInit(final User user) {
        this.user = user;
        //automatic starts changing values - disabling notifications
        isClientChange = false;

        confirmedAccounts = getConfirmedAccountsFor(user);

        changePasswordSection = createChangePasswordSection();
        resetPasswordChannelSection = createResetPasswordChannelSection();
        tfaSection = createTfaSection();

        add(changePasswordSection, resetPasswordChannelSection, tfaSection);

        //automatic set all values, all other actions are from client.
        isClientChange = true;
    }

    private Section createChangePasswordSection() {
        VerticalLayout changePasswordContent = createChangePasswordContent();
        changePasswordSection = new Section("Change Password");
        changePasswordSection.setCentered();
        changePasswordSection.setContent(changePasswordContent);
        return changePasswordSection;
    }

    private VerticalLayout createChangePasswordContent() {
        oldPasswordInput = new PasswordField("Old Password");

        newPasswordInput = new PasswordField("New Password");
        newPasswordInput.addValueChangeListener(this::onPasswordChanged);

        PasswordGenerator passwordGenerator = PasswordGenerator.create();
        passwordGenerator.setOpened(false);
        passwordGenerator.setCopyTarget(newPasswordInput);

        Button updatePasswordButton = new Button("Update");
        updatePasswordButton.addClickListener(this::onPasswordUpdate);
        updatePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updatePasswordButton.setWidthFull();

        VerticalLayout changePasswordLayout = new VerticalLayout(oldPasswordInput, newPasswordInput,
                passwordGenerator, updatePasswordButton);
        changePasswordLayout.setPadding(false);
        changePasswordLayout.setSpacing(false);
        Stream.of(oldPasswordInput, newPasswordInput, updatePasswordButton).forEach(VaadinUtils::setCentered);
        return changePasswordLayout;
    }

    private Section createResetPasswordChannelSection() {
        VerticalLayout content = createResetPasswordChannelContent();
        resetPasswordChannelSection = new Section("Reset password link");
        resetPasswordChannelSection.setCentered();
        resetPasswordChannelSection.setContent(content);
        return resetPasswordChannelSection;
    }

    private VerticalLayout createResetPasswordChannelContent() {
        Span noWhereToSendSpan = getNoWhereToSendSpan();
        TextField resetPasswordField = new TextField();
        resetPasswordField.setReadOnly(true);

        Label sendResetLinkToLabel = new Label("Send reset password link to:");
        resetPasswordSelect = new Select<>();
        resetPasswordSelect.addValueChangeListener(this::onResetPassSelectModified);

        HorizontalLayout resetPasswordFieldLayout = new HorizontalLayout(sendResetLinkToLabel, resetPasswordField);
        VaadinUtils.fitLayoutInWindow(resetPasswordFieldLayout);
        resetPasswordFieldLayout.setAlignItems(Alignment.CENTER);

        HorizontalLayout resetPasswordSelectLayout = new HorizontalLayout(sendResetLinkToLabel, resetPasswordSelect);
        VaadinUtils.fitLayoutInWindow(resetPasswordSelectLayout);
        resetPasswordSelectLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout resetPasswordContent = new VerticalLayout();
        resetPasswordContent.setPadding(false);

        if (confirmedAccounts.isEmpty()) {
            resetPasswordContent.add(noWhereToSendSpan);
        } else if (confirmedAccounts.size() == 1) {
            resetPasswordField.setValue(getAccountTypeName(confirmedAccounts.get(0)));
            resetPasswordContent.add(resetPasswordFieldLayout);
        } else {
            resetPasswordSelect.setItems(confirmedAccounts.stream().map(this::getAccountTypeName).toList());
            resetPasswordContent.add(resetPasswordSelectLayout);
            Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
            userSettings.ifPresent(us -> resetPasswordSelect.setValue(
                    StringUtils.capitalize(userSettings.get().getPasswordResetChannel().name())
            ));
        }

        return resetPasswordContent;
    }

    private Section createTfaSection() {
        VerticalLayout content = createTfaContent();
        tfaSection = new Section("Two-Factor Authentication (2FA)");
        tfaSection.setCentered();
        tfaSection.setContent(content);
        return tfaSection;
    }

    private VerticalLayout createTfaContent() {
        tfaBox = new Checkbox("Protect my account with additional one time codes");
        tfaBox.setValue(userSettingsService.isTfaEnabled(user));
        tfaBox.setEnabled(!confirmedAccounts.isEmpty());
        tfaBox.addValueChangeListener(this::onTfaBoxChanged);

        Span noConfirmedAccountsSpan = getNoConfirmedAccountsSpan();
        Label sentToLabel = new Label("Send to:");

        tfaField = new TextField();
        tfaField.setReadOnly(true);

        HorizontalLayout tfaFieldLayout = new HorizontalLayout(sentToLabel, tfaField);
        VaadinUtils.fitLayoutInWindow(tfaFieldLayout);

        tfaChannelSelect = new Select<>();
        tfaChannelSelect.addValueChangeListener(this::onTfaSelectModified);

        HorizontalLayout tfaSelectLayout = new HorizontalLayout(sentToLabel, tfaChannelSelect);
        VaadinUtils.fitLayoutInWindow(tfaSelectLayout);

        VerticalLayout tfaContent = new VerticalLayout(tfaBox);
        tfaContent.setPadding(false);

        if (confirmedAccounts.isEmpty()) {
            tfaContent.add(noConfirmedAccountsSpan);
        } else if (confirmedAccounts.size() == 1) {
            tfaField.setValue(getAccountTypeName(confirmedAccounts.get(0)));
            tfaContent.add(tfaFieldLayout);
        } else {
            tfaChannelSelect.setItems(confirmedAccounts.stream().map(this::getAccountTypeName).toList());
            tfaContent.add(tfaSelectLayout);
            //set default value
            Optional<UserSettings> userSettings = userSettingsService.getUserSettings(user);
            userSettings.ifPresent(settings -> tfaChannelSelect.setValue(
                    StringUtils.capitalize(userSettings.get().getTfaChannel().name())
            ));
        }

        return tfaContent;
    }

    private void onPasswordChanged(final AbstractField.ComponentValueChangeEvent<PasswordField, String> event) {
        if (StringUtils.isBlank(newPasswordInput.getValue())) {
            newPasswordInput.setInvalid(false);
            newPasswordInput.setErrorMessage("");
        }
        boolean isPasswordValid = !fieldsValidationUtils.isPasswordInvalid(newPasswordInput);
        if (isPasswordValid) {
            newPasswordInput.setInvalid(false);
            newPasswordInput.setErrorMessage("");
        }
    }

    private void onPasswordUpdate(final ClickEvent<Button> event) {
        final String oldPassword = oldPasswordInput.getValue();
        final String newPassword = newPasswordInput.getValue();
        if (StringUtils.isBlank(oldPassword)) {
            onInvalidInput(oldPasswordInput, "Old Password cannot be blank");
            return;
        }

        if (StringUtils.isBlank(newPassword)) {
            onInvalidInput(newPasswordInput, "New Password cannot be blank");
            return;
        }

        final boolean isNewPasswordInvalid = fieldsValidationUtils.isPasswordInvalid(newPasswordInput);
        if (isNewPasswordInvalid) {
            //error message is already set by validator.
            return;
        }

        final boolean isOldPasswordValid = userService.checkPassword(user, oldPassword.trim());
        if (!isOldPasswordValid) {
            onInvalidInput(oldPasswordInput, "Old Password is incorrect");
            return;
        }

        //all field are valid - let's change password
        doPasswordChange();
    }

    private void doPasswordChange() {
        AppUtils.showSuccessNotification("Will do once implemented");
    }

    private void onResetPassSelectModified(final AbstractField.ComponentValueChangeEvent<Select<String>, String> e) {
        AccountType selectedAccountType = AccountType.valueOf(resetPasswordSelect.getValue());
        if (selectedAccountType != AccountType.LOCAL) {
            saveResetPasswordChannel(selectedAccountType);
        }
    }

    private void saveResetPasswordChannel(final AccountType accountType) {
        Optional<UserSettings> userSettings = axeSessionUtils.getCurrentUserSettings();
        if (userSettings.isPresent()) {
            userSettings.get().setPasswordResetChannel(accountType);
            userSettingsService.updateUserSettings(userSettings.get());
            notifyAboutSuccess("Reset Password Channel updated!");
        } else {
            notifyAboutError("Failed to save reset password channel. System error.");
        }
    }

    private void onTfaBoxChanged(final AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> valueChangeEvent) {
        AccountType tfaChannel;
        boolean tfaBoxChecked = tfaBox.getValue();
        if (tfaBoxChecked) {
            //Selecting tfa channel
            if (tfaField.isVisible() && !tfaField.isEmpty()) {
                tfaChannel = AccountType.valueOf(tfaField.getValue());
            } else if (tfaChannelSelect.isVisible()) {
                if (tfaChannelSelect.isEmpty() || tfaChannelSelect.getValue().equals(AccountType.LOCAL.name())) {
                    tfaBox.setValue(false);
                    tfaChannelSelect.setInvalid(true);
                    tfaChannelSelect.setErrorMessage("Please select 2FA destination");
                    tfaChannelSelect.focus();
                    return;
                } else {
                    tfaChannel = AccountType.valueOf(tfaChannelSelect.getValue());
                }
            } else {
                //should never happen
                tfaBox.setValue(false);
                notifyAboutError("Failed to save. No valid and confirmed 2FA channels exist yet");
                return;
            }
        } else {
            //tfa disabled - reset channel
            tfaChannel = AccountType.LOCAL;
        }

        saveTfa(tfaChannel);
    }

    private void onTfaSelectModified(final AbstractField.ComponentValueChangeEvent<Select<String>, String> event) {
        AccountType selectedAccountType = AccountType.valueOf(tfaChannelSelect.getValue());
        if (selectedAccountType != AccountType.LOCAL) {
            tfaBox.setValue(true);
            saveTfa(selectedAccountType);
        } else {
            //should not happen, but no TFA for LOCAL
            tfaBox.setValue(false);
        }
    }

    private void saveTfa(final AccountType tfaChannel) {
        //saving
        Optional<UserSettings> userSettings = userSettingsService.getUserSettings(user);
        if (userSettings.isPresent()) {
            userSettings.get().setTfaEnabled(tfaBox.getValue());
            userSettings.get().setTfaChannel(tfaChannel);
            userSettingsService.updateUserSettings(userSettings.get());
            notifyAboutSuccess("Two-Factor Authentication Settings updated!");
        } else {
            tfaBox.setEnabled(false);
            notifyAboutError("Failed to save 2FA Settings. System error.");
        }
    }

    private List<Account> getConfirmedAccountsFor(final User user) {
        return accountService.getAllAccountsLinkedWithUser(user).stream().filter(Account::isConfirmed).toList();
    }

    private String getAccountTypeName(final Account account) {
        return StringUtils.capitalize(account.getType().name());
    }

    private Span getNoConfirmedAccountsSpan() {
        Span firstPart = new Span("In order to use 2FA, please ");
        Anchor link = new Anchor(Endpoint.UI.CONFIRM_ACCOUNT_PAGE, "confirm account");
        Span lastPart = new Span(".");
        return new Span(firstPart, link, lastPart);
    }

    private Span getNoWhereToSendSpan() {
        Span first = new Span("Nowhere to sent reset password link. Please ");
        Anchor link = new Anchor(Endpoint.UI.CONFIRM_ACCOUNT_PAGE, "confirm account");
        Span last = new Span(".");
        return new Span(first, link, last);
    }

    private void notifyAboutSuccess(final String notificationText) {
        if (isClientChange) {
            AppUtils.showSuccessNotification(notificationText);
        }
    }

    private void notifyAboutError(final String errorText) {
        if (isClientChange) {
            ErrorUtils.showErrorNotification(errorText);
        }
    }
}
