package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.internal.LinkServiceInput;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.user.AuthService;
import io.kyberorg.yalsee.services.user.EmailSenderService;
import io.kyberorg.yalsee.services.user.EmailSenderService.Letter;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class EmailConfirmator implements Confirmator {
    private static final String TAG = "[" + EmailConfirmator.class.getSimpleName() + "]";
    private static final String ERR_EMPTY_TOKEN = "Got empty confirmation token";
    private static final String ERR_EMAIL_NOT_VALID = "Got malformed email";
    private static final String ERR_TOKEN_NOT_FOUND = "Confirmation token not found in the system";
    private static final String ERR_EMAIL_NOT_FOUND = "Email not found in the system";

    private final EmailSenderService emailSenderService;
    private final TokenService tokenService;
    private final AuthService authService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    @Override
    public OperationResult sendConfirmation(final String email, final String confirmationToken) {
        boolean confirmationTokenIsEmpty = StringUtils.isEmpty(confirmationToken);
        if (confirmationTokenIsEmpty) {
            log.warn("{} {}", TAG, ERR_EMPTY_TOKEN);
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_TOKEN);
        }
        boolean isEmailNotValid = !EmailValidator.getInstance().isValid(email);
        if (isEmailNotValid) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_VALID, email);
            return OperationResult.malformedInput().withMessage(ERR_EMAIL_NOT_VALID);
        }
        boolean confirmationTokenNotExist =
                !tokenService.isTokenExists(confirmationToken, TokenType.ACCOUNT_CONFIRMATION_TOKEN);

        if (confirmationTokenNotExist) {
            log.warn("{} {} {}", TAG, ERR_TOKEN_NOT_FOUND, confirmationToken);
            return OperationResult.elementNotFound().withMessage(ERR_TOKEN_NOT_FOUND);
        }
        boolean emailNotExist = !authService.isEmailAlreadyUsed(email);
        if (emailNotExist) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_FOUND, email);
            return OperationResult.elementNotFound().withMessage(ERR_EMAIL_NOT_FOUND);
        }


        final String longConfirmationLink = MessageFormat.format("{0}/{1}?token={2}",
                appUtils.getShortUrl(), Endpoint.UI.CONFIRMATION_PAGE, confirmationToken);
        final OperationResult shortConfirmationLinkResult = makeShortLink(longConfirmationLink);

        MimeMessage letter;
        try {
            final String subject = "Yalsee Confirmation Link";
            Map<String, Object> vars = new HashMap<>(1);

            vars.put("username", getUsername(confirmationToken));
            if (shortConfirmationLinkResult.ok()) {
                vars.put("link", shortConfirmationLinkResult.getStringPayload());
            } else {
                vars.put("link", longConfirmationLink);
                log.warn("{} Shortification failed. Using long link instead {}", TAG, shortConfirmationLinkResult);
            }

            letter = emailSenderService.createLetter(Letter.ACCOUNT_CONFIRMATION, email, subject, vars);
        } catch (Exception e) {
            log.error("{} failed to create email. Reason: {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }

        emailSenderService.sendEmail(email, letter);
        return OperationResult.success();
    }

    private String getUsername(final String tokenString) {
        Optional<Token> token = tokenService.getToken(tokenString);
        if (token.isPresent()) {
            return token.get().getUser().getUsername();
        } else {
            //should never happen
            return "there";
        }
    }

    private OperationResult makeShortLink(final String confirmationLink) {
        String ident;
        do {
            ident = IdentGenerator.generateTokenIdent(TokenType.ACCOUNT_CONFIRMATION_TOKEN);
        } while (linkService.isLinkWithIdentExist(ident).ok());

        LinkServiceInput input = LinkServiceInput.builder(confirmationLink).customIdent(ident).build();
        OperationResult createLinkResult = linkService.createLink(input);
        if (createLinkResult.ok()) {
            //create short link
            final String shortLink = appUtils.getShortUrl() + "/" + ident;
            return OperationResult.success().addPayload(shortLink);
        } else {
            return createLinkResult;
        }
    }

}