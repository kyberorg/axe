package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
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
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/registration_page.css")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Registration - Axe.pm")
public class RegistrationPage extends AxeFormLayout implements BeforeEnterObserver {

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final TextField usernameEmailInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();

    private final Details contactPointDetails = new Details();
    private final EmailField emailField = new EmailField();

    private final Details tfaDetails = new Details();
    private final Checkbox tfaBox = new Checkbox();

    private final Span tosNote = createLegalInfo();

    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
        if (pageAlreadyInitialized) {
            cleanInputs();
        } else {
            pageInit();
            pageAlreadyInitialized = true;
        }
    }
    private void pageInit() {
        setCompactMode();
        setFormTitle("Become Axe User");

        subTitleText.setText("Already have an account? ");

        subTitleLink.setText("Log in");
        subTitleLink.setHref(Endpoint.UI.LOGIN_PAGE);

        setFormSubTitle(subTitleText, subTitleLink);

        usernameEmailInput.setLabel("Username/Email");
        usernameEmailInput.setClearButtonVisible(true);
        passwordInput.setLabel("Password");

        emailField.setLabel("Email");
        emailField.setClassName("email-input");
        contactPointDetails.setSummaryText("Contact point (optional)");
        contactPointDetails.setOpened(false);
        contactPointDetails.addContent(emailField);

        tfaBox.setId("tfaBox");
        tfaBox.setLabel("Protect my account with additional one time codes");
        tfaDetails.setSummaryText("Two-Factor Authentication (2FA)");
        tfaDetails.setOpened(true);
        tfaDetails.addContent(tfaBox);

        setFormFields(usernameEmailInput, passwordInput, contactPointDetails, tfaDetails);

        setComponentsAfterFields(tosNote);
        setSubmitButtonText("Sign up");

        getSubmitButton().addClickListener(this::onRegister);
    }

    private void cleanInputs() {
        usernameEmailInput.clear();
        passwordInput.clear();
        emailField.clear();
    }

    private void onRegister(final ClickEvent<Button> event) {
        Notification.show("Not implemented yet");
    }

    private Span createLegalInfo() {
        Span tosStart = new Span("By signing up, you accept our ");
        Anchor linkToTerms = new Anchor();
        linkToTerms.setHref(Endpoint.UI.TOS_PAGE);
        linkToTerms.setText("Terms of Service");

        Span tosEnd = new Span(".");

        return new Span(tosStart, linkToTerms, tosEnd);
    }
}
