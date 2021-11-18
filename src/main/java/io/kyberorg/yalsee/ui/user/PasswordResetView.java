package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static io.kyberorg.yalsee.ui.core.YalseeFormLayout.START_POINT;

@Slf4j
@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.PASSWORD_RESET_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Password Reset Page")
public class PasswordResetView extends Div implements HasUrlParameter<String> {
    private static final String TAG = "[" + PasswordResetView.class.getSimpleName() + "]";

    private static final String DIRECT_MESSAGE = "Not intended for direct use";
    private static final String NO_PARAMS_MESSAGE = "Not intended for use without required parameters";

    private final TokenService tokenService;
    private final UserService userService;

    private final YalseeLayout yalseeLayout = new YalseeLayout();
    private final Span banner = new Span();

    private final YalseeFormLayout formLayout = new YalseeFormLayout();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField repeatPasswordField = new PasswordField();

    private User user;

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter String parameter) {
        Component coreLayout;
        if (requestHasNoParams(event)) {
            coreLayout = yalseeLayoutWithMessage(DIRECT_MESSAGE);
        } else if (isTokenParamsPresent(event)) {
            String token = getToken(event);
            boolean isTokenExists = tokenService.isTokenExists(token, TokenType.PASSWORD_RESET_TOKEN);
            boolean isTokenExpired = tokenService.isTokenExpired(token);
            if (token != null && isTokenExists && !isTokenExpired) {
                if (tokenService.getToken(token).isPresent()) {
                    coreLayout = getResetPasswordForm(tokenService.getToken(token).get().getUser());
                    OperationResult tokenDeletionResult = tokenService.deleteToken(token);
                    if (tokenDeletionResult.notOk()) {
                        log.warn("{} failed to delete token. Reason: {}", TAG, tokenDeletionResult);
                    }
                } else {
                    //should not happen
                    coreLayout = yalseeLayoutWithMessage("Internal System Error");
                }
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

    private Component getResetPasswordForm(final User user) {
        this.user = user;

        formLayout.setCompactMode();
        formLayout.setFormTitle("Password Reset");

        final FormLayout passwordFields = new FormLayout();

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
        formLayout.getSubmitButton().addClickListener(this::onSubmit);

        return formLayout;
    }

    private void onSubmit(ClickEvent<Button> event) {
        final String password = passwordField.getValue();
        final String repeatPassword = repeatPasswordField.getValue();

        if (StringUtils.isBlank(repeatPassword)) {
            ErrorUtils.showError("Password confirmation cannot be empty");
            return;
        }
        if (!repeatPassword.equals(password)) {
            ErrorUtils.showError("Password and confirmation are different");
            return;
        }
        OperationResult validationResult = userService.validatePassword(password);
        if (validationResult.notOk()) {
            ErrorUtils.showError(validationResult.getMessage());
            return;
        }
        OperationResult passwordResetResult = userService.resetPassword(user, password);
        if (passwordResetResult.ok()) {
            cleanFields();
            showSuccessBanner();
        } else {
            log.error("{} failed to update password", TAG);
            ErrorUtils.showError("Failed to update password. System error. Try again later");
        }
    }

    private void cleanFields() {
        passwordField.clear();
        repeatPasswordField.clear();
    }

    private void showSuccessBanner() {
        Notification notification = new Notification();

        Span span = new Span("Password successfully updated. Now you can login with new password");
        Button loginPageButton = new Button("To Login Page", e -> {
            notification.close();
            if (e.getSource().getUI().isPresent()) {
                final UI ui = e.getSource().getUI().get();
                ui.navigate(Endpoint.UI.LOGIN_PAGE);
            }
        });

        HorizontalLayout notificationLayout = new HorizontalLayout();
        notificationLayout.setWidthFull();
        notificationLayout.add(span, loginPageButton);

        notification.add(notificationLayout);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);

        span.getStyle().set("margin-right", "0.5rem");
        span.getStyle().set("align-self", "center");
        loginPageButton.getStyle().set("margin-right", "0.5rem");

        notification.open();
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
