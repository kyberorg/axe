package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.TfaService;
import io.kyberorg.yalsee.services.user.UserPreferencesService;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Login Page.
 *
 * @since 4.0
 */
@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Login Page")
public class LoginView extends YalseeFormLayout {
    public static final String TAG = "[" + LoginView.class.getSimpleName() + "]";

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final FormLayout fields = new FormLayout();
    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox rememberMe = new Checkbox();

    private final UserService userService;
    private final UserPreferencesService userPreferencesService;
    private final TfaService tfaService;

    public LoginView(UserService userService, UserPreferencesService userPreferencesService, TfaService tfaService) {
        this.userService = userService;
        this.userPreferencesService = userPreferencesService;
        this.tfaService = tfaService;
        setId(IDs.PAGE_ID);
        init();
    }

    private void init() {
        setCompactMode();
        setFormTitle("Sign in to Yalsee");

        subTitleText.setId(LoginView.IDs.SUBTITLE_TEXT);
        subTitleText.setText("New to Yalsee? ");

        subTitleLink.setId(LoginView.IDs.SUBTITLE_LINK);
        subTitleLink.setText("Register here");
        subTitleLink.setHref(Endpoint.UI.REGISTRATION_PAGE);
        setFormSubTitle(subTitleText, subTitleLink);

        usernameInput.setId(IDs.USERNAME_INPUT);
        passwordInput.setId(IDs.PASSWORD_INPUT);

        rememberMe.setId(IDs.REMEMBER_ME);
        rememberMe.setLabel("Remember my username");

        fields.addFormItem(usernameInput, "Username");
        fields.addFormItem(passwordInput, "Password");
        fields.add(rememberMe);
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep(START_POINT, 1));

        addFormFields(fields);
        setSubmitButtonText("Jump in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onLogin);
        enableForgotPasswordLink();
    }

    private void onLogin(ClickEvent<Button> buttonClickEvent) {
        String username = usernameInput.getValue();
        String password = passwordInput.getValue();

        log.info("{} new login attempt. Username: {}", TAG, username);

        User user;
        try {
            user = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("{} username not found. Username {}", TAG, username);
            ErrorUtils.showError("Wrong credentials");
            return;
        }

        boolean isPasswordCorrect = userService.checkPassword(user, password);

        if (!isPasswordCorrect) {
            log.warn("{} password incorrect for username {}", TAG, username);
            ErrorUtils.showError("Wrong credentials");
            return;
        }
        boolean isAccountLocked = user.isLocked();
        if (isAccountLocked) {
            log.warn("{} Account is locked. Username {}", TAG, username);
            ErrorUtils.showError("Account locked");
            return;
        }
        //all good - logging user in
        log.info("{} login succeed, username {}", TAG, username);
        VaadinSession.getCurrent().setAttribute(App.Session.USER_KEY, user);
        boolean isTFAEnabled = userPreferencesService.isTfaEnabled(user);
        String navigationTarget;
        if (isTFAEnabled) {
            OperationResult sendVerificationCodeResult = tfaService.sendVerificationCode(user);
            if (sendVerificationCodeResult.ok()) {
                navigationTarget = Endpoint.UI.VERIFICATION_PAGE;
            } else {
                log.warn("{} 2fa code send failed. Error: {}", TAG, sendVerificationCodeResult.getMessage());
                ErrorUtils.showError("Failed to send Two-Factor Authentication code. Try again later.");
                navigationTarget = null;
            }
        } else {
            navigationTarget = Endpoint.UI.HOME_PAGE;
        }
        if (navigationTarget != null) {
            UI.getCurrent().navigate(navigationTarget);
        }
    }

    public static final class IDs {
        public static final String PAGE_ID = "loginPage";
        public static final String SUBTITLE_TEXT = "subtitleText";
        public static final String SUBTITLE_LINK = "subtitleLink";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REMEMBER_ME = "rememberMe";
    }
}
