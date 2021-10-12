package io.kyberorg.yalsee.services.user.confirmators;

import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.EmailSenderService;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EmailConfirmator implements Confirmator {
    private final EmailSenderService emailSenderService;

    @Override
    public OperationResult sendConfirmation(final String email, final String confirmationToken) {
        boolean isEmailValid = EmailValidator.getInstance().isValid(email);
        if (isEmailValid) {
            //TODO make letter
            emailSenderService.sendEmail(email);
            return OperationResult.success();
        } else {
            return OperationResult.malformedInput();
        }
    }
}
