package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pm.axe.db.models.Account;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.ui.elements.TelegramSpan;
import pm.axe.users.AccountType;

import java.util.Optional;

@RequiredArgsConstructor
@SpringComponent
@UIScope
public class ProfileTab extends VerticalLayout implements HasTabInit {
    private final AccountService accountService;
    private final TokenService tokenService;
    private User user;

    @Override
    public void tabInit(final User user) {
        this.user = user;

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
        Details howEmailUsedDetails = new Details("What will be sent to email?");
        Span span = new Span("Account Recovery"); //TODO replace with UL with usage - see RegForm for UL example
        howEmailUsedDetails.setOpened(false);
        howEmailUsedDetails.setContent(span);

        //telegram section
        FlexLayout telegramLayout = new FlexLayout();
        telegramLayout.setAlignItems(Alignment.BASELINE);

        Optional<Account> telegramAccount = getTelegramAccount();
        Optional<String> telegramUsername = getTelegramUsername();
        if (telegramAccount.isPresent() && telegramUsername.isPresent()) { //FIXME fix logic
            TextField telegramField = new TextField("Telegram");
            telegramField.setPrefixComponent(VaadinIcon.AT.create());
            telegramField.setValue(telegramUsername.get());
            Button unlink = new Button("Unlink");
            telegramLayout.add(telegramField, unlink);
        } else {
            Optional<Token> tgToken = getTelegramToken();
            if (tgToken.isPresent()) {
                TelegramSpan telegramSpan = TelegramSpan.create(tgToken.get());
                telegramLayout.add(telegramSpan);
            } else {
                telegramLayout.setVisible(false);
            }
        }

        add(usernameLayout, emailLayout, howEmailUsedDetails, telegramLayout);
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

    private Optional<String> getTelegramUsername() {
        Optional<Account> telegramAccount = getTelegramAccount();
        if (telegramAccount.isPresent()) {
            return accountService.decryptAccountName(telegramAccount.get());
        } else {
            return Optional.empty();
        }
    }
}
