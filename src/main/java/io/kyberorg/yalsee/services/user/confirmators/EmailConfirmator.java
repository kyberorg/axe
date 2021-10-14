package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.AuthService;
import io.kyberorg.yalsee.services.user.EmailSenderService;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@AllArgsConstructor
@Component
public class EmailConfirmator implements Confirmator {
    private static final String TAG = "[" + EmailConfirmator.class + "]";
    private static final String ERR_EMPTY_TOKEN = "Got empty confirmation token";
    private static final String ERR_EMAIL_NOT_VALID = "Got malformed email";
    private static final String ERR_TOKEN_NOT_FOUND = "Confirmation token not found in the system";
    private static final String ERR_EMAIL_NOT_FOUND = "Email not found in the system";

    private final EmailSenderService emailSenderService;
    private final TokenService tokenService;
    private final AuthService authService;
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
            return OperationResult.elementNotFound().withMessage(ERR_EMPTY_TOKEN);
        }
        boolean emailNotExist = !authService.isEmailAlreadyUsed(email);
        if (emailNotExist) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_FOUND, email);
            return OperationResult.elementNotFound().withMessage(ERR_EMAIL_NOT_FOUND);
        }

        SimpleMailMessage letter = makeLetter(email, confirmationToken);
        emailSenderService.sendEmail(email, letter);
        return OperationResult.success();
    }

    private SimpleMailMessage makeLetter(final String email, final String token) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(getFromAddress());
        mailMessage.setTo(email);
        mailMessage.setSubject("Yalsee Confirmation Link");
        mailMessage.setText(MessageFormat.format(" Please click below to active your account. {0}/{1}?token={2}",
                appUtils.getServerUrl(), Endpoint.UI.CONFIRMATION_PAGE, token));
        return mailMessage;
    }

    private String getFromAddress() {
        return appUtils.getEnv().getProperty(App.Properties.MAIL_FROM_ADDRESS, App.Defaults.MAIL_FROM_ADDRESS);
    }
}
