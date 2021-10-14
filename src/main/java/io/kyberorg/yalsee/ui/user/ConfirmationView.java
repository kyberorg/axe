package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.AuthService;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.components.Result;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.VaadinParamUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.CONFIRMATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Confirmation Page")
public class ConfirmationView extends Div implements HasUrlParameter<String> {
    private static final String DIRECT_MESSAGE = "Not intended for direct use";
    private static final String NO_PARAMS_MESSAGE = "Not intended for use without required parameters";

    private final YalseeLayout yalseeLayout = new YalseeLayout();
    private final Span banner = new Span();

    private final TokenService tokenService;
    private final AuthService authService;

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        Component coreLayout;
        if (VaadinParamUtils.requestHasNoParams(event)) {
            coreLayout = yalseeLayoutWithMessage(DIRECT_MESSAGE);
        } else if (VaadinParamUtils.isParamPresent(App.Params.TOKEN, event)) {
            String token = VaadinParamUtils.getParamValue(App.Params.TOKEN, event);
            boolean confirmationTokenExists = tokenService.isTokenExists(token, TokenType.ACCOUNT_CONFIRMATION_TOKEN);
            boolean tokenExpired = tokenService.isTokenExpired(token);

            if (token != null && confirmationTokenExists && !tokenExpired) {
                OperationResult confirmationResult = authService.confirmAccount(token);
                if (confirmationResult.ok()) {
                    coreLayout = getSuccess();
                } else {
                    coreLayout = yalseeLayoutWithMessage("Failed to confirm account. Internal system error. " +
                            "Please try again later.");
                }
            } else {
                coreLayout = noSuchTokenLayout();
            }
        } else {
            //no params
            coreLayout = yalseeLayoutWithMessage(NO_PARAMS_MESSAGE);
        }
        add(coreLayout);
    }

    private YalseeLayout yalseeLayoutWithMessage(final String message) {
        banner.setText(message);
        yalseeLayout.add(banner);
        yalseeLayout.getCentralLayout().setAlignItems(FlexComponent.Alignment.CENTER);
        return yalseeLayout;
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

    private Component getSuccess() {
        Result result = new Result(true);
        result.setSuccessText("Account Confirmed");
        yalseeLayout.add(result);
        return yalseeLayout;
    }

}
