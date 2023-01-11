package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
@CssImport(value = "./css/login_page.css")
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Login Page - Axe.pm")
public class LoginPage extends AxeFormLayout implements BeforeEnterObserver {
    private final Span subTitleText = new Span();
    private final Span spaceSpan = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final VerticalLayout fields = new VerticalLayout();
    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox forgotMe = new Checkbox();

    private final Section forgotPasswordSection = new Section();
    private final Anchor forgotPasswordLink = new Anchor();

    private boolean pageAlreadyInitialized = false;
    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        if (pageAlreadyInitialized) return;
        init();
        pageAlreadyInitialized = true;
    }

    private void init() {
        setCompactMode();
        setFormTitle("Sign in to Axe");
        subTitleText.setText("New to Axe?");
        spaceSpan.setText(" ");
        subTitleLink.setText("Register here");
        subTitleLink.setHref(Endpoint.UI.REGISTRATION_PAGE);

        setFormSubTitle(subTitleText, spaceSpan, subTitleLink);

        usernameInput.setLabel("Username/Email");
        passwordInput.setLabel("Password");
        forgotMe.setLabel("Log me out after");
        fields.add(usernameInput, passwordInput, forgotMe);
        fields.getChildren().forEach(component -> component.getElement().setAttribute("class", "form-input"));
        fields.setSpacing(false);
        fields.addClassName("vertically-compact");

        setFormFields(fields);

        setSubmitButtonText("Jump in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onLogin);

        forgotPasswordLink.setHref(Endpoint.UI.FORGOT_PASSWORD_PAGE);
        forgotPasswordLink.setText("Forgot your password?");
        forgotPasswordLink.setClassName("forgot-password-link");
        forgotPasswordSection.add(forgotPasswordLink);
        forgotPasswordSection.setClassName("forgot-password-section");

        setComponentsAfterSubmitButton(forgotPasswordSection);

        //TODO small screen only
        getCentralLayout().getStyle().set("padding-left", "1px");
        getCentralLayout().getStyle().set("padding-right", "1px");

    }

    private void onLogin(final ClickEvent<Button> event) {
        Notification.show("Not implemented yet");
    }
}
