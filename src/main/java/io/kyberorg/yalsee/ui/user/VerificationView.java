package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.VERIFICATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Verification Page")
public class VerificationView extends YalseeFormLayout {
    public static final String TAG = "[" + VerificationView.class.getSimpleName() + "]";

    private final FormLayout fields = new FormLayout();
    private final TextField codeInput = new TextField();

    private final TokenService tokenService;
    private final LoginView loginView;

    public VerificationView(TokenService tokenService, LoginView loginView) {
        this.tokenService = tokenService;
        this.loginView = loginView;
        init();
    }

    private void init() {
        setCompactMode();
        setFormTitle("Verification Code");

        codeInput.setId(IDs.CODE_INPUT);
        codeInput.setAutofocus(true);
        fields.addFormItem(codeInput, "Code (OTP)");
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep(START_POINT, 1));

        addFormFields(fields);
        setAdditionalInfo("Please insert your verification code");
        setSubmitButtonText("Let me in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onSubmitButtonClicked);
    }

    private void onSubmitButtonClicked(ClickEvent<Button> buttonClickEvent) {
        String code = codeInput.getValue();
        boolean codeExists = tokenService.isTokenExists(code, TokenType.LOGIN_VERIFICATION_TOKEN);
        if (!codeExists) {
            ErrorUtils.showError("Wrong code");
            return;
        }
        boolean codeExpired = tokenService.isTokenExpired(code);
        if (codeExpired) {
            ErrorUtils.showError("Code expired");
            return;
        }
        Optional<Token> token = tokenService.getToken(code);
        if (token.isEmpty()) {
            ErrorUtils.showError("Internal Error");
            return;
        }
        //all good storing user and deleting token
        boolean isForgotMeModeActivated;
        if (VaadinSession.getCurrent() != null) {
            isForgotMeModeActivated = (boolean) VaadinSession.getCurrent().getAttribute(App.Session.FORGOT_ME_KEY);
        } else {
            isForgotMeModeActivated = false;
        }
        User user = token.get().getUser();
        loginView.logUserIn(user, isForgotMeModeActivated);

        OperationResult deleteResult = tokenService.deleteToken(code);
        if (deleteResult.notOk()) {
            log.warn("{} unable to delete verification code '{}' from database. Error is {}",
                    TAG, code, deleteResult.getMessage());
        }
        UI.getCurrent().navigate(Endpoint.UI.HOME_PAGE);
    }

    public static final class IDs {
        public static final String CODE_INPUT = "codeInput";
    }
}
