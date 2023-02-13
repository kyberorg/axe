package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Endpoint;
import pm.axe.db.models.Token;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;


@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/login_page.css")
@Route(value = Endpoint.UI.LOGIN_VERIFICATION_PAGE, layout = MainView.class)
@PageTitle("Login Verification - Axe.pm")
public class LoginVerificationPage extends AxeFormLayout implements BeforeEnterObserver {

    private final TextField otpInput = new TextField();
    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        if (pageAlreadyInitialized) {
            cleanInputs();
        } else {
            pageInit();
            pageAlreadyInitialized = true;
        }
    }

    private void cleanInputs() {
        otpInput.clear();
    }

    private void pageInit() {
        setCompactMode();
        setFormTitle("Verification Code\n");

        otpInput.setLabel("Code (OTP)");

        setComponentsAfterFields(new Span("Please insert your verification code"));
        setSubmitButtonText("Let me in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onSubmit);
    }

    private void onSubmit(final ClickEvent<Button> event) {
        String otp = otpInput.getValue();
        if (StringUtils.isBlank(otp)) {
            otpInput.setInvalid(true);
            otpInput.setErrorMessage("Please enter code");
            otpInput.focus();
            return;
        }

        if (otp.length() != Token.CODE_TOKEN_LEN) {
            fail();
            return;
        }

        //FIXME update code once 2FA logic ready
        int code;
        try {
            code = Integer.parseInt(otp);
        } catch (NumberFormatException e) {
            fail();
        }
        //TODO check otp token from db
        //TODO paste token owner to session
    }

    private void fail() {
        Notification.show("Wrong Code");
    }
}
