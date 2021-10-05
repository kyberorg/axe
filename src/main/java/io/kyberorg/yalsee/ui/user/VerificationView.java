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
@Route(value = Endpoint.UI.VERIFICATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Verification Page")
public class VerificationView extends YalseeFormLayout {

    private final FormLayout fields = new FormLayout();
    private final TextField codeInput = new TextField();

    public VerificationView() {
        init();
        applyStyle();
    }

    private void init() {
        setFormTitle("Verification Code");

        codeInput.setId(IDs.CODE_INPUT);
        fields.addFormItem(codeInput, "Code (OTP)");
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep(START_POINT, 1));

        addFormFields(fields);
        setAdditionalInfo("Please insert your verification code");
        setSubmitButtonText("Let me in");
    }

    private void applyStyle() {

    }

    public static final class IDs {
        public static final String CODE_INPUT = "codeInput";
    }
}
