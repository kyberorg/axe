package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.core.SymmetricCryptTool;
import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.AuthorizationDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class AuthService {
    public static final String TAG = "[" + AuthService.class.getSimpleName() + "]";
    private final AuthorizationDao authorizationDao;
    private final SymmetricCryptTool cryptTool;

    public static final String OP_EMPTY_EMAIL = "Email cannot be empty";
    public static final String OP_EMAIL_NOT_VALID = "Please use valid email address";
    public static final String OP_EMAIL_ALREADY_EXISTS = "Email already used";
    public static final String ERR_ENCRYPTION_FAILED = "Failed to encrypt plain text value before saving";

    public boolean isEmailAlreadyUsed(final String email) {
        if (StringUtils.isBlank(email)) return false;

        List<Authorization> emailAuthorizations = authorizationDao.findByProvider(AuthProvider.EMAIL);
        Authorization sameEmail = emailAuthorizations.parallelStream()
                .filter(encryptedEmail ->
                        cryptTool.decrypt(encryptedEmail.getAuthUsername()).getStringPayload().equals(email))
                .findFirst()
                .orElse(null);
        return Objects.nonNull(sameEmail);
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

    public OperationResult createLocalAuthorization(final User user) {
        Authorization localAuthorization = new Authorization();
        localAuthorization.setUser(user);
        localAuthorization.setProvider(AuthProvider.LOCAL);
        localAuthorization.setAuthUsername(user.getUsername());
        localAuthorization.setConfirmed(false);

        try {
            authorizationDao.save(localAuthorization);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail();
        }
    }

    public OperationResult createEmailAuthority(final User user, final String email) {
        OperationResult emailValidationResult = validateEmail(email);
        if (emailValidationResult.notOk()) {
            return emailValidationResult;
        }

        String encryptedEmail;
        OperationResult encryptEmailResult = cryptTool.encrypt(email);
        if (encryptEmailResult.ok()) {
            encryptedEmail = encryptEmailResult.getStringPayload();
        } else {
            log.error("{} email encryption failed. Value: {}. Error: {}", TAG, email, encryptEmailResult.getMessage());
            return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
        }

        Authorization emailAuthorization = new Authorization();
        emailAuthorization.setUser(user);
        emailAuthorization.setProvider(AuthProvider.EMAIL);
        emailAuthorization.setAuthUsername(encryptedEmail);
        emailAuthorization.setConfirmed(false);

        try {
            authorizationDao.save(emailAuthorization);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail();
        }
    }
}
