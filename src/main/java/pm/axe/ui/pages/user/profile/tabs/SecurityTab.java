package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
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
import pm.axe.services.user.UserSettingsService;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.users.AccountType;
import pm.axe.utils.AxeSessionUtils;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@SpringComponent
@UIScope
public class SecurityTab extends VerticalLayout implements HasTabInit {
    private final AccountService accountService;
    private final UserSettingsService userSettingsService;
    private final AxeSessionUtils axeSessionUtils;

    private Select<String> resetPasswordSelect;

    private Checkbox tfaBox;
    private TextField tfaField;
    private Select<String> tfaChannelSelect;

    private User user;

    @Override
    public void tabInit(final User user) {
        this.user = user;
        List<Account> confirmedAccounts = getConfirmedAccountsFor(user);

        //change password section
        H4 changePasswordTitle = new H4("Change Password");
        PasswordField oldPasswordInput = new PasswordField("Old Password");
        PasswordField newPasswordInput = new PasswordField("New Password");
        PasswordGenerator passwordGenerator = PasswordGenerator.create();
        passwordGenerator.setOpened(false);
        passwordGenerator.setCopyTarget(newPasswordInput);
        Button updatePasswordButton = new Button("Update");
        VerticalLayout changePasswordLayout = new VerticalLayout(changePasswordTitle,
                oldPasswordInput, newPasswordInput, passwordGenerator,
                updatePasswordButton);
        changePasswordLayout.setPadding(false);
        changePasswordLayout.setSpacing(false);

        Hr separator = new Hr();

        //reset password channel session
        H4 resetPasswordTitle = new H4("Reset password link");
        Span noWhereToSendSpan = getNoWhereToSendSpan();
        TextField resetPasswordField = new TextField();
        resetPasswordField.setReadOnly(true);

        Label sendResetLinkToLabel = new Label("Send reset password link to:");
        resetPasswordSelect = new Select<>();
        resetPasswordSelect.addValueChangeListener(this::onResetPasswordSelectModified);

        HorizontalLayout resetPasswordFieldLayout = new HorizontalLayout(sendResetLinkToLabel, resetPasswordField);
        resetPasswordFieldLayout.setAlignItems(Alignment.BASELINE);
        HorizontalLayout resetPasswordSelectLayout = new HorizontalLayout(sendResetLinkToLabel, resetPasswordSelect);
        resetPasswordSelectLayout.setAlignItems(Alignment.BASELINE);

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

        VerticalLayout resetPasswordLayout = new VerticalLayout(resetPasswordTitle, resetPasswordContent);
        resetPasswordLayout.setPadding(false);

        Hr separator2 = new Hr();

        //tfa section
        H4 tfaTitle = new H4("Two-Factor Authentication (2FA)");

        tfaBox = new Checkbox("Protect my account with additional one time codes");
        tfaBox.setValue(userSettingsService.isTfaEnabled(user));
        tfaBox.setEnabled(!confirmedAccounts.isEmpty());
        tfaBox.addValueChangeListener(this::onTfaBoxChanged);

        Span noConfirmedAccountsSpan = getNoConfirmedAccountsSpan();
        String sendTo = "Send to"; //TODO kinda static string

        tfaField = new TextField();
        tfaField.setLabel(sendTo);
        tfaField.setReadOnly(true);


        tfaChannelSelect = new Select<>();
        tfaChannelSelect.setLabel(sendTo);
        tfaChannelSelect.addValueChangeListener(this::onTfaSelectModified);

        VerticalLayout tfaContent = new VerticalLayout(tfaBox);
        tfaContent.setPadding(false);

        if (confirmedAccounts.isEmpty()) {
            tfaContent.add(noConfirmedAccountsSpan);
        } else if (confirmedAccounts.size() == 1) {
            tfaField.setValue(getAccountTypeName(confirmedAccounts.get(0)));
            tfaContent.add(tfaField);
        } else {
            tfaChannelSelect.setItems(confirmedAccounts.stream().map(this::getAccountTypeName).toList());
            tfaContent.add(tfaChannelSelect);
            //set default value
            Optional<UserSettings> userSettings = userSettingsService.getUserSettings(user);
            userSettings.ifPresent(settings -> tfaChannelSelect.setValue(
                    StringUtils.capitalize(userSettings.get().getTfaChannel().name())
            ));
        }

        VerticalLayout tfaLayout = new VerticalLayout(tfaTitle, tfaBox, tfaContent);
        tfaLayout.setPadding(false);

        add(changePasswordLayout, separator, resetPasswordLayout, separator2, tfaLayout);
    }

    private void onResetPasswordSelectModified(final AbstractField.ComponentValueChangeEvent<Select<String>, String> event) {
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
            Notification.show("Saved");
        } else {
            Notification.show("Failed to save. System error.");
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
                Notification.show("Failed to save. No valid and confirmed 2FA channels exist yet");
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
            Notification.show("Saved");
        } else {
            tfaBox.setEnabled(false);
            Notification.show("Failed to save. System error.");
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
}
