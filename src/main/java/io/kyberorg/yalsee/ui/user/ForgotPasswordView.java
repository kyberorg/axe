package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.FORGOT_PASSWORD_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Forgot Password Page")
public class ForgotPasswordView extends YalseeFormLayout {
    public static final String TAG = "[" + ForgotPasswordView.class.getSimpleName() + "]";

    private final FormLayout fields = new FormLayout();
    private final TextField usernameInput = new TextField();

    private final UserService userService;

    public ForgotPasswordView(final UserService userService) {
        this.userService = userService;
        init();
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
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onSubmit);
    }

    private void onSubmit(final ClickEvent<Button> event) {
        final String username = usernameInput.getValue();
        log.info("{} password reset attempt. Username: {}", TAG, username);

        User user;
        try {
            user = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("{} username not found. Username {}", TAG, username);
            showSuccessMessage();
            return;
        }

        final OperationResult sendPasswordResetCode = userService.sendPasswordResetCode(user);
        if (sendPasswordResetCode.ok()) {
            showSuccessMessage();
        } else {
            if (sendPasswordResetCode.getResult().equals(OperationResult.SYSTEM_DOWN)) {
                ErrorUtils.showError("");
            } else {
                showSuccessMessage();
            }
        }
    }

    private void showSuccessMessage() {
        Notification notification = new Notification("Password reset link successfully sent");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

    public static final class IDs {
        public static final String USERNAME_INPUT = "usernameInput";
    }
}
