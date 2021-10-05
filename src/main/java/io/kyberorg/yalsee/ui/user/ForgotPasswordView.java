package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.FORGOT_PASSWORD_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Forgot Password Page")
public class ForgotPasswordView extends YalseeFormLayout {

    private final FormLayout fields = new FormLayout();
    private final TextField usernameInput = new TextField();

    public ForgotPasswordView() {
        init();
        applyStyle();
    }

    private void init() {
        setCompactMode();
        setFormTitle("Forgot Password?");

        usernameInput.setId(IDs.USERNAME_INPUT);
        fields.addFormItem(usernameInput, "Username");
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep(START_POINT, 1));

        addFormFields(fields);
        setAdditionalInfo("We will send password reset link to " +
                "confirmation channel (email etc.) linked with given account.");
        setSubmitButtonText("Submit");
    }

    private void applyStyle() {

    }

    public static final class IDs {
        public static final String USERNAME_INPUT = "usernameInput";
    }
}
