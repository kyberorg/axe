package io.kyberorg.yalsee.services.user.verificationsenders;

import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.AuthService;
import io.kyberorg.yalsee.services.user.EmailSenderService;
import io.kyberorg.yalsee.services.user.EmailSenderService.Letter;
import io.kyberorg.yalsee.services.user.TokenService;
import io.kyberorg.yalsee.services.user.confirmators.EmailConfirmator;
import io.kyberorg.yalsee.users.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailVerificationSender implements VerificationSender {
    private static final String TAG = "[" + EmailConfirmator.class.getSimpleName() + "]";
    private static final String ERR_EMPTY_CODE = "Got empty verification code";
    private static final String ERR_EMAIL_NOT_VALID = "Got malformed email";
    private static final String ERR_CODE_NOT_FOUND = "Verification code not found in the system";
    private static final String ERR_EMAIL_NOT_FOUND = "Email not found in the system";
    private static final String ERR_FAILED_TO_CREATE_EMAIL = "Failed to create email";

    private final EmailSenderService emailSenderService;
    private final TokenService tokenService;
    private final AuthService authService;

    @Override
    public OperationResult sendVerification(String email, String verificationCode) {
        boolean verificationCodeIsEmpty = StringUtils.isEmpty(verificationCode);

        if (verificationCodeIsEmpty) {
            log.warn("{} {}", TAG, ERR_EMPTY_CODE);
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_CODE);
        }
        boolean isEmailNotValid = !EmailValidator.getInstance().isValid(email);
        if (isEmailNotValid) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_VALID, email);
            return OperationResult.malformedInput().withMessage(ERR_EMAIL_NOT_VALID);
        }
        boolean verificationCodeNotExist =
                !tokenService.isTokenExists(verificationCode, TokenType.LOGIN_VERIFICATION_TOKEN);

        if (verificationCodeNotExist) {
            log.warn("{} {} {}", TAG, ERR_CODE_NOT_FOUND, verificationCode);
            return OperationResult.elementNotFound().withMessage(ERR_CODE_NOT_FOUND);
        }
        boolean emailNotExist = !authService.isEmailAlreadyUsed(email);
        if (emailNotExist) {
            log.warn("{} {} {}", TAG, ERR_EMAIL_NOT_FOUND, email);
            return OperationResult.elementNotFound().withMessage(ERR_EMAIL_NOT_FOUND);
        }

        MimeMessage letter;
        try {
            final String subject = "Yalsee Verification Code";
            Map<String, Object> vars = new HashMap<>(1);
            vars.put("code", verificationCode);

            letter = emailSenderService.createLetter(Letter.OTP, email, subject, vars);
        } catch (Exception e) {
            log.error("{} failed to create email. Reason: {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(ERR_FAILED_TO_CREATE_EMAIL);
        }

        emailSenderService.sendEmail(email, letter);
        return OperationResult.success();
    }


}
