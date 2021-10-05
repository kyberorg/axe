package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.formlayout.FormLayout;
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

    private final FormLayout fields = new FormLayout();
    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();

    public LoginView() {
        setId(IDs.PAGE_ID);
        init();
    }

    private void init() {
        setCompactMode();
        setFormTitle("Log in");

        usernameInput.setId(IDs.USERNAME_INPUT);
        passwordInput.setId(IDs.PASSWORD_INPUT);

        fields.addFormItem(usernameInput, "Username");
        fields.addFormItem(passwordInput, "Password");
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep(START_POINT, 1));

        addFormFields(fields);
        setSubmitButtonText("Log in");
        enableForgotPasswordLink();
    }

    public static final class IDs {
        public static final String PAGE_ID = "loginPage";
        public static final String USERNAME_INPUT = "usernameInput";
        public static final String PASSWORD_INPUT = "passwordInput";
    }
}
