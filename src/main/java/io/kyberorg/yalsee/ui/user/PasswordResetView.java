package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import static io.kyberorg.yalsee.ui.core.YalseeFormLayout.START_POINT;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.PASSWORD_RESET_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Password Reset Page")
public class PasswordResetView extends Div implements HasUrlParameter<String> {
    private static final String DIRECT_MESSAGE = "Not intended for direct use";
    private static final String NO_PARAMS_MESSAGE = "Not intended for use without required parameters";

    private final YalseeLayout yalseeLayout = new YalseeLayout();
    private final Span banner = new Span();

    private final YalseeFormLayout formLayout = new YalseeFormLayout();

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter String parameter) {
        Component coreLayout;
        if (requestHasNoParams(event)) {
            coreLayout = yalseeLayoutWithMessage(DIRECT_MESSAGE);
        } else if (isTokenParamsPresent(event)) {
            //TODO control token via Server
            String token = getToken(event);
            if (token != null && token.equals("TmpToken")) {
                coreLayout = getResetPasswordForm();
            } else {
                //no such token exception
                coreLayout = noSuchTokenLayout();
            }
        } else {
            //no params
            coreLayout = yalseeLayoutWithMessage(NO_PARAMS_MESSAGE);
        }

        add(coreLayout);
    }

    private Component getResetPasswordForm() {
        formLayout.setCompactMode();
        formLayout.setFormTitle("Password Reset");

        final FormLayout passwordFields = new FormLayout();

        final PasswordField passwordField = new PasswordField();
        final PasswordField repeatPasswordField = new PasswordField();

        passwordField.setId(IDs.PASSWORD_INPUT);
        repeatPasswordField.setId(IDs.REPEAT_PASSWORD_INPUT);

        passwordFields.addFormItem(passwordField, "Password");
        passwordFields.addFormItem(repeatPasswordField, "Confirm Password");

        passwordFields.setResponsiveSteps(
                new FormLayout.ResponsiveStep(START_POINT, 1)
        );

        //remove previous layout (when you go back to this page)
        formLayout.removeFormFields(passwordFields);
        formLayout.addFormFields(passwordFields);

        formLayout.setSubmitButtonText("Reset password");

        return formLayout;
    }

    private YalseeLayout noSuchTokenLayout() {
        final H1 title = new H1("No Such Token Exception");
        final Span subTitle = new Span("Provided token is expired, not valid or never existed. Really.");
        final Image image = new Image("images/noToken.png", "No Token Image");

        image.addClassName("error-image");
        yalseeLayout.add(title, subTitle, image);
        yalseeLayout.getCentralLayout().setAlignItems(FlexComponent.Alignment.CENTER);
        return yalseeLayout;
    }

    private YalseeLayout yalseeLayoutWithMessage(final String message) {
        banner.setText(message);
        yalseeLayout.add(banner);
        yalseeLayout.getCentralLayout().setAlignItems(FlexComponent.Alignment.CENTER);
        return yalseeLayout;
    }

    private boolean requestHasNoParams(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().isEmpty();
    }

    private boolean isTokenParamsPresent(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().containsKey(App.Params.TOKEN);
    }

    private String getToken(final BeforeEvent event) {
        if (isTokenParamsPresent(event)) {
            QueryParameters queryParameters = event.getLocation().getQueryParameters();
            if (queryParameters.getParameters().get(App.Params.TOKEN).size() > 0) {
                return queryParameters.getParameters().get(App.Params.TOKEN).get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static final class IDs {
        public static final String PASSWORD_INPUT = "passwordInput";
        public static final String REPEAT_PASSWORD_INPUT = "repeatPasswordInput";
    }
}
