package io.kyberorg.yalsee.services.user.passresetsenders;

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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Component
public class EmailPassResetSender implements PassResetSender {
    private static final String TAG = "[" + EmailPassResetSender.class.getSimpleName() + "]";
    private static final String ERR_EMPTY_CODE = "Got empty password reset token";
    private static final String ERR_EMAIL_NOT_VALID = "Got malformed email";
    private static final String ERR_CODE_NOT_FOUND = "Password reset token not found in the system";
    private static final String ERR_EMAIL_NOT_FOUND = "Email not found in the system";
    private static final String ERR_FAILED_TO_CREATE_EMAIL = "Failed to create email";

    private final EmailSenderService emailSenderService;
    private final TokenService tokenService;
    private final AuthService authService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    @Override
    public OperationResult sendResetLink(String email, String passwordResetToken) {
        boolean passwordResetTokenIsEmpty = StringUtils.isEmpty(passwordResetToken);

        if (passwordResetTokenIsEmpty) {
            log.warn("{} {}", TAG, ERR_EMPTY_CODE);
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_CODE);
        }
        boolean isEmailNotValid = !EmailValidator.getInstance().isValid(email);
        if (isEmailNotValid) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_VALID, email);
            return OperationResult.malformedInput().withMessage(ERR_EMAIL_NOT_VALID);
        }
        boolean passwordResetTokenNotExist =
                !tokenService.isTokenExists(passwordResetToken, TokenType.PASSWORD_RESET_TOKEN);

        if (passwordResetTokenNotExist) {
            log.warn("{} {} {}", TAG, ERR_CODE_NOT_FOUND, passwordResetToken);
            return OperationResult.elementNotFound().withMessage(ERR_CODE_NOT_FOUND);
        }
        boolean emailNotExist = !authService.isEmailAlreadyUsed(email);
        if (emailNotExist) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_FOUND, email);
            return OperationResult.elementNotFound().withMessage(ERR_EMAIL_NOT_FOUND);
        }

        final String longPasswordResetLink = MessageFormat.format("{0}/{1}?token={2}",
                appUtils.getShortUrl(), Endpoint.UI.PASSWORD_RESET_PAGE, passwordResetToken);
        final OperationResult shortPasswordResetLinkResult = makeShortLink(longPasswordResetLink);

        MimeMessage letter;
        try {
            final String subject = "Yalsee Password Reset Request";
            Map<String, Object> vars = new HashMap<>(2);
            vars.put("username", getUsername(passwordResetToken));
            if (shortPasswordResetLinkResult.ok()) {
                vars.put("link", shortPasswordResetLinkResult.getStringPayload());
            } else {
                vars.put("link", longPasswordResetLink);
                log.warn("{} Shortification failed. Using long link instead {}",
                        TAG, shortPasswordResetLinkResult);
            }

            letter = emailSenderService.createLetter(Letter.PASSWORD_RESET, email, subject, vars);
        } catch (Exception e) {
            log.error("{} failed to create email. Reason: {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(ERR_FAILED_TO_CREATE_EMAIL);
        }

        emailSenderService.sendEmail(email, letter);
        return OperationResult.success();
    }

    private Object getUsername(String passwordResetToken) {
        Optional<Token> token = tokenService.getToken(passwordResetToken);
        if (token.isPresent()) {
            return token.get().getUser().getUsername();
        } else {
            //should never happen
            return "there";
        }
    }

    private OperationResult makeShortLink(String longPasswordResetLink) {
        String ident;
        do {
            ident = IdentGenerator.generateTokenIdent(TokenType.PASSWORD_RESET_TOKEN);
        } while (linkService.isLinkWithIdentExist(ident).ok());

        LinkServiceInput input = LinkServiceInput.builder(longPasswordResetLink).customIdent(ident).build();
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
