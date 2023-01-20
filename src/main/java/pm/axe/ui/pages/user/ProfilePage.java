package pm.axe.ui.pages.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
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
import pm.axe.Endpoint;
import pm.axe.db.models.Account;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserSettingsService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.users.AccountType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/profile_page.css")
@Route(value = Endpoint.UI.PROFILE_PAGE, layout = MainView.class)
@PageTitle("My Profile - Axe.pm")
public class ProfilePage extends AxeCompactLayout implements BeforeEnterObserver {
    private final AccountService accountService;
    private final UserSettingsService userSettingsService;
    private boolean pageAlreadyInitialized = false;
    private User user;
    private List<Account> confirmedAccounts;

    private Checkbox tfaBox;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        boundUserIfAny();
        if (Objects.isNull(user)) {
            event.forwardTo(LoginPage.class);
            return;
        }
        confirmedAccounts = getConfirmedAccountsFor(user);

        if (!pageAlreadyInitialized) {
            initPage();
            pageAlreadyInitialized = true;
        }
    }

    private void initPage() {
        //title
        H2 title = new H2("My Profile");
        title.setClassName("profile-title");

        //username
        TextField username = new TextField("Username");
        username.setValue(user.getUsername());
        username.setReadOnly(true);
        Button editUsernameButton = new Button("Edit");
        Button saveUsernameButton = new Button("Save");
        FlexLayout usernameLayout = new FlexLayout(username, editUsernameButton);
        usernameLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        usernameLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        //TODO replace with methods and fix button replace login - see MyLinksPage
        editUsernameButton.addClickListener(e -> {
            username.setReadOnly(false);
            usernameLayout.replace(editUsernameButton, saveUsernameButton);
        });
        saveUsernameButton.addClickListener(e -> {
           username.setReadOnly(true);
           usernameLayout.replace(saveUsernameButton, editUsernameButton);
        });

        //email
        EmailField emailField = new EmailField("E-mail");
        emailField.setValue(getCurrentEmail());
        emailField.setReadOnly(true);

        Button editEmailButton = new Button("Edit");
        Button saveEmailButton = new Button("Save");
        FlexLayout emailLayout = new FlexLayout(emailField, editEmailButton);
        emailLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        emailLayout.setAlignItems(Alignment.BASELINE);

        //TODO replace with methods and fix button replace login - see MyLinksPage
        editEmailButton.addClickListener(e -> {
           emailField.setReadOnly(false);
           emailLayout.replace(editEmailButton, saveEmailButton);
        });
        saveEmailButton.addClickListener(e -> {
           emailField.setReadOnly(true);
           emailLayout.replace(saveEmailButton, editEmailButton);
        });

        //how Axe use email details
        Details howEmailUsedDetails = new Details("What will be send to email?");
        Span span = new Span("Account Recovery"); //TODO replace with UL with usage - see RegForm for UL example
        howEmailUsedDetails.setOpened(false);
        howEmailUsedDetails.setContent(span);

        //TODO tg section
        Hr firstSeparator = new Hr();
        //change password section
        H4 changePasswordTitle = new H4("Change Password");
        PasswordField oldPasswordInput = new PasswordField("Old Password");
        PasswordField newPasswordInput = new PasswordField("New Password");
        PasswordGenerator passwordGenerator = PasswordGenerator.create();
        passwordGenerator.setOpened(false);
        passwordGenerator.setCopyTarget(newPasswordInput);
        Button updatePasswordButton = new Button("Update");
        VerticalLayout passwordLayout = new VerticalLayout(changePasswordTitle,
                oldPasswordInput, newPasswordInput, passwordGenerator,
                updatePasswordButton);

        //second separator
        Hr secondSeparator = new Hr();

        //tfa section
        H5 tfaTitle = new H5("Two-Factor Authentication (2FA)");

        tfaBox = new Checkbox("Protect my account with additional one time codes");
        tfaBox.setValue(userSettingsService.isTfaEnabled(user));
        tfaBox.setEnabled(!confirmedAccounts.isEmpty());
        tfaBox.addValueChangeListener(this::onTfaBoxChanged);

        Span noConfirmedAccountsSpan = getNoConfirmedAccountsSpan();
        TextField tfaField = new TextField();
        tfaField.setReadOnly(true);

        Label sendToLabel = new Label("Send to:");
        Select<String> tfaChannelSelect = new Select<>();
        Button saveTfaChannelButton = new Button("Save");

        HorizontalLayout tfaSelectLayout = new HorizontalLayout(sendToLabel, tfaChannelSelect, saveTfaChannelButton);
        tfaSelectLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout tfaFieldLayout = new HorizontalLayout(sendToLabel, tfaField);
        tfaFieldLayout.setAlignItems(Alignment.CENTER);

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
        }

        //TODO 3rd separator
        //TODO Settings Tab/Zone
        //TODO danger zone

        //overall layout
        add(title, usernameLayout, emailLayout, howEmailUsedDetails,
                firstSeparator, passwordLayout,
                secondSeparator, tfaTitle, tfaContent);
    }

    private void onTfaBoxChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> valueChangeEvent) {
        Optional<UserSettings> userSettings = userSettingsService.getUserSettings(user);
        if (userSettings.isPresent()) {
            userSettings.get().setTfaEnabled(tfaBox.getValue());
            userSettingsService.updateUserSettings(userSettings.get());
            Notification.show("Saved");
        } else {
            tfaBox.setEnabled(false);
            Notification.show("Failed to save. System error.");
        }
    }

    private void boundUserIfAny() {
        Optional<AxeSession> axeSession = AxeSession.getCurrent();
        if (axeSession.isPresent()) {
            if (axeSession.get().hasUser()) {
                user = axeSession.get().getUser();
            }
        }
    }

    //TODO maybe Optional<String> instead
    private String getCurrentEmail() {
        Optional<Account> emailAccount = accountService.getAccount(user, AccountType.EMAIL);
        if (emailAccount.isPresent()) {
            Optional<String> plainTextEmail = accountService.decryptAccountName(emailAccount.get());
            if (plainTextEmail.isPresent() && StringUtils.isNotBlank(plainTextEmail.get())) {
                return plainTextEmail.get();
            } else {
                return "";
            }
        } else {
            return "";
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
        Span lastPart = new Span(" .");
        return new Span(firstPart, link, lastPart);
    }
}
