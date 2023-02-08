package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.services.user.TokenService;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.Section;
import pm.axe.ui.elements.TelegramSpan;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.utils.AxeSessionUtils;
import pm.axe.utils.VaadinUtils;

import java.util.Objects;
import java.util.Optional;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@Route(value = Endpoint.UI.CONFIRM_ACCOUNT_PAGE, layout = MainView.class)
@PageTitle("Confirm Account - Axe.pm")
public class ConfirmAccountPage extends AxeCompactLayout implements BeforeEnterObserver {
    private final TokenService tokenService;
    private final AxeSessionUtils axeSessionUtils;
    private boolean pageAlreadyInitialized = false;
    private User user;

    private HorizontalLayout emailLayout;
    private EmailField emailInput;
    private Button submitEmailButton;
    private Span sentSpan;

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
        H3 title = new H3("Confirm your Account");

        Section emailSection =  new Section("Using Email");
        emailSection.setCustomContent(emailSectionContent());

        telegramSection = new Section("Using Telegram");
        telegramSection.setCustomContent(telegramSectionContent());

        add(title, emailSection, telegramSection);
    }

    private Component emailSectionContent() {
        emailInput = new EmailField("Email address here");
        emailInput.setHelperText("Axe will send confirmation letter there");
        submitEmailButton = new Button("Submit");
        submitEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitEmailButton.addClickListener(this::onSubmitEmail);
        sentSpan = new Span("Sent!");
        sentSpan.setClassName("green");

        emailLayout = new HorizontalLayout(emailInput, submitEmailButton);
        VaadinUtils.fitLayoutInWindow(emailLayout);
        VaadinUtils.setSmallSpacing(emailLayout);
        return emailLayout;
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

    private void onSubmitEmail(ClickEvent<Button> event) {
        if (emailInput.isInvalid()) {
            emailInput.setInvalid(true);
            emailInput.setErrorMessage("Please enter valid email");
            return;
        }
        //TODO implement sent confirmation letter call here
        emailInput.setReadOnly(true);
        emailLayout.replace(submitEmailButton, sentSpan);
    }
}