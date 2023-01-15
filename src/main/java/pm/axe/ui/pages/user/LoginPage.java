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
import pm.axe.db.models.User;
import pm.axe.services.user.UserService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.utils.AppUtils;

import java.util.Optional;


@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/login_page.css")
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Login Page - Axe.pm")
public class LoginPage extends AxeFormLayout implements BeforeEnterObserver {
    private final AppUtils appUtils;
    private final UserService userService;
    private final Span subTitleText = new Span();
    private final Span spaceSpan = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox forgotMe = new Checkbox();

    private final Section forgotPasswordSection = new Section();
    private final Anchor forgotPasswordLink = new Anchor();

    private boolean pageAlreadyInitialized = false;
    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
        if (pageAlreadyInitialized) {
           cleanInputs();
        } else {
            init();
            pageAlreadyInitialized = true;
        }
    }

    private void init() {
        setCompactMode();
        getForm().getStyle().set("width", "min(380px, 100%) !important"); //dirty fix for large screens

        setFormTitle("Sign in to Axe");
        subTitleText.setText("New to Axe?");
        spaceSpan.setText(" ");
        subTitleLink.setText("Register here");
        subTitleLink.setHref(Endpoint.UI.REGISTRATION_PAGE);

        setFormSubTitle(subTitleText, spaceSpan, subTitleLink);

        usernameInput.setLabel("Username/Email");
        usernameInput.setClearButtonVisible(true);
        passwordInput.setLabel("Password");
        forgotMe.setLabel("Log me out after");

        setFormFields(usernameInput, passwordInput, forgotMe);

        setSubmitButtonText("Jump in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onLogin);

        forgotPasswordLink.setHref(Endpoint.UI.FORGOT_PASSWORD_PAGE);
        forgotPasswordLink.setText("Forgot your password?");
        forgotPasswordLink.setClassName("forgot-password-link");
        forgotPasswordSection.add(forgotPasswordLink);
        forgotPasswordSection.setClassName("forgot-password-section");

        setComponentsAfterSubmitButton(forgotPasswordSection);
    }

    private void onLogin(final ClickEvent<Button> event) {
        //TODO do fields validation before sending anything
        //FIXME remove after real login progress implemented
        if (appUtils.isDevelopmentModeActivated()) {
            doEasyLogin();
        } else {
            Notification.show("Not implemented yet");
        }
    }

    private void doEasyLogin() {
        if (userService.isUserExists(usernameInput.getValue())) {
            AxeSession.getCurrent().ifPresent(axs -> {
                Optional<User> user = userService.getUserByUsername(usernameInput.getValue());
                user.ifPresent(axs::setUser);
                usernameInput.getUI().ifPresent(ui -> ui.navigate(Endpoint.UI.PROFILE_PAGE));
            });
        } else {
            usernameInput.setInvalid(true);
            usernameInput.setErrorMessage("No such user found");
        }
    }

    private void cleanInputs() {
        usernameInput.clear();
        passwordInput.clear();
        forgotMe.clear();
    }
}
