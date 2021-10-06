package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;

/**
 * Login Page.
 *
 * @since 4.0
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Login Page")
public class LoginView extends YalseeFormLayout {

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final FormLayout fields = new FormLayout();
    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox rememberMe = new Checkbox();

    public LoginView() {
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
        enableForgotPasswordLink();
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
