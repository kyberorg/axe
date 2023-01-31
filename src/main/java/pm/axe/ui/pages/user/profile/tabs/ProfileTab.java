package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import pm.axe.ui.elements.Section;
import pm.axe.ui.elements.TelegramSpan;
import pm.axe.users.AccountType;
import pm.axe.utils.VaadinUtils;

import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@SpringComponent
@UIScope
public class ProfileTab extends VerticalLayout implements HasTabInit {
    private final AccountService accountService;
    private final TokenService tokenService;
    private User user;

    @SuppressWarnings("FieldCanBeLocal") //will be re-drawn if event received
    private Section accountsSection;

    private final TextField usernameField = new TextField();
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
    }

    private Section createAccountSection() {
        Component usernameLayout = createUsernameLayout();
        Component emailLayout = createEmailLayout();
        Details emailUsageDetails = createEmailUsageDetails();
        Component telegramLayout = createTelegramLayout();

        Section section = new Section("Accounts");
        section.setContent(usernameLayout, emailLayout, emailUsageDetails, telegramLayout);
        section.setCentered();
        return section;
    }

    private HorizontalLayout createUsernameLayout() {
        usernameField.setLabel("Username");
        usernameField.setValue(user.getUsername());
        usernameField.setReadOnly(true);

        editUsernameButton.setText("Edit");
        editUsernameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveUsernameButton.setText("Save");
        saveUsernameButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        usernameLayout.add(usernameField, editUsernameButton);
        VaadinUtils.fitLayoutInWindow(usernameLayout);
        VaadinUtils.setSmallSpacing(usernameLayout);

        editUsernameButton.addClickListener(this::onEditUsername);
        saveUsernameButton.addClickListener(this::onSaveUsername);
        return usernameLayout;
    }

    private Component createEmailLayout() {
        emailField.setLabel("E-mail");
        Optional<String> currentEmail = getCurrentEmail();
        emailField.setReadOnly(true);
        currentEmail.ifPresent(e -> emailField.setValue(currentEmail.get()));

        editEmailButton.setText("Edit");
        editEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveEmailButton.setText("Save");
        saveEmailButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        emailLayout.add(emailField, editEmailButton);
        VaadinUtils.fitLayoutInWindow(emailLayout);
        VaadinUtils.setSmallSpacing(emailLayout);

        editEmailButton.addClickListener(this::onEditEmail);
        saveEmailButton.addClickListener(this::onSaveEmail);
        return emailLayout;
    }

    private HorizontalLayout createTelegramLayout() {
        HorizontalLayout telegramLayout = new HorizontalLayout();
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
        Span span = new Span("Account Recovery"); //TODO replace with UL with usage - see RegForm for UL example
        howEmailUsedDetails.setOpened(false);
        howEmailUsedDetails.setContent(span);
        return howEmailUsedDetails;
    }

    private void onEditUsername(final ClickEvent<Button> event) {
        usernameField.setReadOnly(false);
        usernameLayout.replace(editUsernameButton, saveUsernameButton);
    }

    private void onSaveUsername(final ClickEvent<Button> event) {
        usernameField.setReadOnly(true);
        usernameLayout.replace(saveUsernameButton, editUsernameButton);
    }

    private void onEditEmail(ClickEvent<Button> event) {
        emailField.setReadOnly(false);
        emailLayout.replace(editEmailButton, saveEmailButton);
    }

    private void onSaveEmail(ClickEvent<Button> event) {
        emailField.setReadOnly(true);
        emailLayout.replace(saveEmailButton, editEmailButton);
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
