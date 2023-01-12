package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.utils.AppUtils;

@Slf4j
@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/forgot_password_page.css")
@Route(value = Endpoint.UI.FORGOT_PASSWORD_PAGE, layout = MainView.class)
@PageTitle("Forgot Password - Axe.pm")
public class ForgotPasswordPage extends AxeFormLayout implements BeforeEnterObserver {
    private static final String TAG = "[" + ForgotPasswordPage.class.getSimpleName() + "]";
    private final AppUtils appUtils;

    private final TextField usernameInput = new TextField();
    private Span contactKyberorgSpan;
    private final Span successSpan = getSuccessSpan();

    private Details nothingLinkedDetails;

    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
        if (pageAlreadyInitialized) return;
        pageInit();
        applyState();
        pageAlreadyInitialized = true;
    }

    private void pageInit() {
        setCompactMode();
        setFormTitle("Forgot Password?");

        usernameInput.setLabel("Username/Email");
        usernameInput.setClearButtonVisible(true);
        setFormFields(usernameInput);

        contactKyberorgSpan = getContactKyberorgSpan();
        setComponentsAfterFields(getRecoveryNote());
        setSubmitButtonText("Submit");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onSubmit);
    }

    private void applyState() {
        usernameInput.focus();
        nothingLinkedDetails.setOpened(false);
    }

    private void onSubmit(final ClickEvent<Button> event) {
        if (nothingLinkedDetails.isOpened()) nothingLinkedDetails.setOpened(false);

        final String username = usernameInput.getValue();
        if (StringUtils.isBlank(username)) {
            usernameInput.setInvalid(true);
            usernameInput.setErrorMessage("Please enter username");
            return;
        }
        log.info("{} Password reset attempt. Username: '{}'", TAG, username);
        cleanAnHideInput();
        showSuccessMessage();
    }

    private void cleanAnHideInput() {
        usernameInput.setValue("");
        usernameInput.setVisible(false);
    }

    private void showSuccessMessage() {
        replaceSubmitButtonWithComponents(successSpan);
    }

    private VerticalLayout getRecoveryNote() {
        VerticalLayout recoveryNote = new VerticalLayout();
        Span infoSpan = new Span("We will send password reset link to contact point (email etc.) " +
                "linked with given account.");

        VerticalLayout nothingLinkedContent = getAccordionContent();

        nothingLinkedDetails = new Details("Nothing linked with account?", nothingLinkedContent);

        recoveryNote.add(infoSpan, nothingLinkedDetails);
        return recoveryNote;
    }
    private VerticalLayout getAccordionContent() {
        VerticalLayout content = new VerticalLayout();
        Span manualWaySpan = new Span("Manual way is an only way to recover it.");
        content.add(manualWaySpan, contactKyberorgSpan);
        return content;
    }

    private Span getContactKyberorgSpan() {
        Span contact = new Span("Contact ");
        String kyberorg = "kyberorg";
        String telegramLink = String.format("%s%s", Axe.Telegram.TELEGRAM_URL, kyberorg);
        Anchor tgLink = new Anchor(telegramLink, "@" + kyberorg);

        Span dropEmailText = new Span(" or drop email to ");
        String axeEmail = appUtils.getEmailFromAddress();
        String mailTo = String.format("mailto:%s", axeEmail);
        Anchor emailLink = new Anchor(mailTo, axeEmail);

        return new Span(contact, tgLink, dropEmailText, emailLink);
    }

    private Span getSuccessSpan() {
        Span successSpan = new Span();
        successSpan.setClassName("green");
        successSpan.addClassName("result-span");
        successSpan.setText("Sent");
        return successSpan;
    }
}

