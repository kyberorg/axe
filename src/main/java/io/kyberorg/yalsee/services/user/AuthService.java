package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.dao.AuthorizationDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final AuthorizationDao authorizationDao;

    public static final String OP_EMPTY_EMAIL = "Email cannot be empty";
    public static final String OP_EMAIL_NOT_VALID = "Please use valid email address";
    public static final String OP_EMAIL_ALREADY_EXISTS = "Email already used";

    public boolean isEmailAlreadyUsed(final String email) {
        if (StringUtils.isBlank(email)) return false;
        return authorizationDao.existsByProviderAndAuthUsername(AuthProvider.EMAIL, email);
    }

    public OperationResult validateEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return OperationResult.malformedInput().withMessage(OP_EMPTY_EMAIL);
        }

        boolean isEmailValid = EmailValidator.getInstance().isValid(email);
        if (!isEmailValid) {
            return OperationResult.malformedInput().withMessage(OP_EMAIL_NOT_VALID);
        }

        if (isEmailAlreadyUsed(email)) {
            return OperationResult.conflict().withMessage(OP_EMAIL_ALREADY_EXISTS);
        }
        return OperationResult.success();
    }
}
