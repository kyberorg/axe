package pm.axe.ui.pages.user;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.constants.App;
import pm.axe.db.models.Token;
import pm.axe.services.user.TokenService;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.users.TokenType;
import pm.axe.utils.VaadinUtils;

import java.util.Optional;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.CONFIRMATION_PAGE, layout = MainView.class)
@PageTitle("Account Confirmation - Axe.pm")
public class ConfirmationView extends AxeBaseLayout implements HasUrlParameter<String> {
    private final TokenService tokenService;
    private static final String DIRECT_MESSAGE = "Not intended for direct use";

    public ConfirmationView(final TokenService tokenService) {
        this.tokenService = tokenService;
        Span directUseBanner = new Span(DIRECT_MESSAGE);
        directUseBanner.setId("directUseBanner");
        add(directUseBanner);
    }

    @Override
    public void setParameter(final BeforeEvent beforeEvent, @OptionalParameter final String parameter) {
        boolean isTokenParamPresent = VaadinUtils.isParamPresent(App.Params.TOKEN, beforeEvent);
        if (!isTokenParamPresent) {
            redirectToRegistrationFailedPage(beforeEvent);
            return;
        }
        Optional<String> tokenString = VaadinUtils.getParamValue(App.Params.TOKEN, beforeEvent);
        if (tokenString.isEmpty()) {
            redirectToRegistrationFailedPage(beforeEvent);
            return;
        }
        Optional<Token> token = tokenService.getToken(tokenString.get());
        if (token.isEmpty()) {
            redirectToRegistrationFailedPage(beforeEvent);
            return;
        }
        boolean tokenHasCorrectType = token.get().getTokenType() == TokenType.ACCOUNT_CONFIRMATION_TOKEN;
        boolean tokenHasLinkedAccount = token.get().getConfirmationFor() != null;
        if (tokenHasCorrectType && tokenHasLinkedAccount) {
            //confirm account
            //confirm user - if not confirmed yet
            //delete token
            //rdr to welcome page
            beforeEvent.rerouteTo(Endpoint.UI.WELCOME_PAGE);
        } else {
            redirectToRegistrationFailedPage(beforeEvent);
        }
    }

    private void redirectToRegistrationFailedPage(final BeforeEvent beforeEvent) {
        beforeEvent.rerouteTo(Endpoint.UI.REGISTRATION_FAILED_PAGE);
    }
}
