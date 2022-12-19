package pm.axe.ui.pages.user;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pm.axe.Endpoint;
import pm.axe.constants.App;
import pm.axe.db.models.Token;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.TokenService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.users.TokenType;
import pm.axe.utils.AppUtils;
import pm.axe.utils.VaadinUtils;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * User Confirmation entrypoint. It redirects to {@link WelcomePage} or {@link RegistrationFailedPage}.
 */
@Slf4j
@SpringComponent
@RequiredArgsConstructor
@UIScope
@Route(value = Endpoint.UI.CONFIRMATION_PAGE, layout = MainView.class)
@PageTitle("Account Confirmation - Axe.pm")
public class ConfirmationView extends AxeBaseLayout implements HasUrlParameter<String> {
    private static final String TAG = "[" + ConfirmationView.class.getSimpleName() + "]";
    private final TokenService tokenService;
    private final AccountService accountService;
    private final AppUtils appUtils;
    private static final String DIRECT_MESSAGE = "Not intended for direct use";

    /**
     * Inits components with their default values.
     */
    @PostConstruct
    public void init() {
        Span directUseBanner = new Span(DIRECT_MESSAGE);
        directUseBanner.setId("directUseBanner");
        add(directUseBanner);
    }

    @Override
    public void setParameter(final BeforeEvent beforeEvent, @OptionalParameter final String parameter) {
        boolean isTokenParamPresent = VaadinUtils.isParamPresent(App.Params.TOKEN, beforeEvent);
        if (!isTokenParamPresent) {
            log.warn("{} Token param absent", TAG);
            return;
        }
        Optional<String> tokenString = VaadinUtils.getParamValue(App.Params.TOKEN, beforeEvent);
        if (tokenString.isEmpty()) {
            log.warn("{} Token is empty", TAG);
            redirectToRegistrationFailedPage(beforeEvent);
            return;
        }
        Optional<Token> token = tokenService.getToken(tokenString.get());
        if (token.isEmpty()) {
            log.warn("{} Token doesn't exist or expired", TAG);
            redirectToRegistrationFailedPage(beforeEvent);
            return;
        }
        boolean tokenHasCorrectType = token.get().getTokenType() == TokenType.ACCOUNT_CONFIRMATION_TOKEN;
        boolean tokenHasLinkedAccount = token.get().getConfirmationFor() != null;
        if (tokenHasCorrectType && tokenHasLinkedAccount) {
            //confirm account
            OperationResult confirmationResult = accountService.confirmAccount(token.get().getConfirmationFor());
            if (confirmationResult.notOk()) {
                log.error("{} Failed to confirm account. OpResult: {}", TAG, confirmationResult);
                //creating task to delete confirmation token. We should delete it even if confirmation failed.
                tokenService.deleteTokenRecord(token.get());
                //failing with error
                throw new RuntimeException("Failed to confirm account. Got Server-side error");
            }
            //delete token (async operation)
            tokenService.deleteTokenRecord(token.get());
            //store User to AxeSession
            AxeSession.getCurrent().ifPresent(as -> as.setUserId(token.get().getConfirmationFor().getUser().getId()));
            //rdr to welcome page
            redirectToWelcomePage(beforeEvent);
        } else {
            log.warn("{} Token has wrong type. Got: {}, excepted: {}. Or linked account is gone.",
                    TAG, token.get().getTokenType(), TokenType.ACCOUNT_CONFIRMATION_TOKEN);
            redirectToRegistrationFailedPage(beforeEvent);
        }
    }

    private void redirectToWelcomePage(final BeforeEvent beforeEvent) {
        beforeEvent.forwardToUrl(appUtils.getServerUrl() + "/" + Endpoint.UI.WELCOME_PAGE);
    }

    private void redirectToRegistrationFailedPage(final BeforeEvent beforeEvent) {
        beforeEvent.forwardToUrl(appUtils.getServerUrl() + "/" + Endpoint.UI.REGISTRATION_FAILED_PAGE);
    }
}
