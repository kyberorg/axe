package io.kyberorg.yalsee.services.mail;

import io.kyberorg.yalsee.result.OperationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

/**
 * Service that handles email issues.
 */
@RequiredArgsConstructor
@Service
public class EmailService {
    private static final String OP_EMPTY_EMAIL = "Email cannot be empty";
    private static final String OP_EMAIL_NOT_VALID = "Please use valid email address";

    /**
     * Check if email address valid.
     *
     * @param email string with email address to validate.
     * @return {@link OperationResult#success()} if email valid
     * or {@link OperationResult#malformedInput()} with error message.
     */
    public OperationResult isEmailValid(final String email) {
        if (StringUtils.isBlank(email)) {
            return OperationResult.malformedInput().withMessage(OP_EMPTY_EMAIL);
        }

        boolean isEmailValid = EmailValidator.getInstance().isValid(email);
        if (!isEmailValid) {
            return OperationResult.malformedInput().withMessage(OP_EMAIL_NOT_VALID);
        }

        return OperationResult.success();
    }
}
