package pm.axe.ui.pages.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
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
import pm.axe.services.user.AccountService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.PasswordGenerator;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.users.AccountType;

import java.util.List;
import java.util.Optional;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/profile_page.css")
@Route(value = Endpoint.UI.PROFILE_PAGE, layout = MainView.class)
@PageTitle("My Profile - Axe.pm")
public class ProfilePage extends AxeCompactLayout implements BeforeEnterObserver {
    private final AccountService accountService;
    private boolean pageAlreadyInitialized = false;
    private User user;
    private List<Account> confirmedAccounts;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        boundUserOrGoToLogin(event);
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
        username.setEnabled(false);
        Button editUsernameButton = new Button("Edit");
        Button saveUsernameButton = new Button("Save");
        FlexLayout usernameLayout = new FlexLayout(username, editUsernameButton);
        usernameLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        usernameLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        //TODO replace with methods and fix button replace login - see MyLinksPage
        editUsernameButton.addClickListener(e -> {
            username.setEnabled(true);
            usernameLayout.replace(editUsernameButton, saveUsernameButton);
        });
        saveUsernameButton.addClickListener(e -> {
           username.setEnabled(false);
           usernameLayout.replace(saveUsernameButton, editUsernameButton);
        });

        //email
        EmailField emailField = new EmailField("E-mail");
        emailField.setValue(getCurrentEmail());

        Button saveEmailButton = new Button("Save");
        FlexLayout emailLayout = new FlexLayout(emailField, saveEmailButton);
        emailLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        emailLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        //how Axe use email details
        Details howEmailUsedDetails = new Details("How Axe use email?");
        Span span = new Span("Account Recovery"); //TODO replace with UL with usage
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
        Details tfaDetails = new Details("Two-Factor Authentication (2FA)");
        Checkbox tfaBox = new Checkbox("Protect my account with additional one time codes");
        tfaBox.setEnabled(!confirmedAccounts.isEmpty());

        Select<AccountType> tfaChannelSelect = new Select<>();
        tfaChannelSelect.setItems(confirmedAccounts.stream().map(Account::getType).toList());
        Button saveTfaChannelButton = new Button("Save");
        HorizontalLayout tfaChannelLayout = new HorizontalLayout(tfaChannelSelect, saveTfaChannelButton);

        VerticalLayout tfaContent = new VerticalLayout(tfaBox);
        if (confirmedAccounts.size() > 1) {
            tfaContent.add(tfaChannelLayout);
        }
        tfaDetails.setContent(tfaContent);
        tfaDetails.setOpened(true);

        //TODO 3rd separator
        //TODO danger zone

        //overall layout
        add(title, usernameLayout, emailLayout, howEmailUsedDetails,
                firstSeparator, passwordLayout,
                secondSeparator, tfaDetails);
    }

    private void boundUserOrGoToLogin(BeforeEnterEvent event) {
        Optional<AxeSession> axeSession = AxeSession.getCurrent();
        if (axeSession.isPresent()) {
            if (axeSession.get().hasUser()) {
                user = axeSession.get().getUser();
            } else {
                event.forwardTo(Endpoint.UI.LOGIN_PAGE);
            }
        } else {
            event.forwardTo(LoginPage.class);
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
}
