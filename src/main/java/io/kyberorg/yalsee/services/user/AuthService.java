package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.core.SymmetricCryptTool;
import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.AuthorizationDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    public static final String TAG = "[" + AuthService.class.getSimpleName() + "]";
    private static final String ERR_NO_SUCH_TOKEN_FOUND = "No such token found";
    private static final String ERR_TOKEN_HAS_NO_AUTHORIZATION = "Token has no authorization";
    private static final String ERR_USER_HAS_NO_LOCAL_AUTH = "User has no local authorization";

    private final AuthorizationDao authorizationDao;
    private final SymmetricCryptTool cryptTool;
    private final TokenService tokenService;

    public static final String OP_EMPTY_EMAIL = "Email cannot be empty";
    public static final String OP_EMAIL_NOT_VALID = "Please use valid email address";
    public static final String OP_EMAIL_ALREADY_EXISTS = "Email already used";
    public static final String ERR_ENCRYPTION_FAILED = "Failed to encrypt plain text value before saving";

    private String emailToSearch;

    public boolean isEmailAlreadyUsed(final String email) {
        if (StringUtils.isBlank(email)) return false;
        this.emailToSearch = email;

        List<Authorization> emailAuthorizations = authorizationDao.findByProvider(AuthProvider.EMAIL);
        Authorization sameEmail = emailAuthorizations.parallelStream()
                .filter(this::authorizationHasGivenEmailAddress)
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
            return OperationResult.success().addPayload(emailAuthorization);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail();
        }
    }

    private boolean authorizationHasGivenEmailAddress(Authorization authorization) {
        OperationResult result = cryptTool.decrypt(authorization.getAuthUsername());
        if (result.ok()) {
            String valueFromDb = result.getStringPayload();
            if (StringUtils.isNotBlank(valueFromDb)) {
                return valueFromDb.equals(emailToSearch);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public OperationResult confirmAccount(String tokenString) {
        try {
            //search token - Op.notFound()
            Optional<Token> token = tokenService.getToken(tokenString);
            if (token.isEmpty()) {
                log.info("{} token {} not found", TAG, tokenString);
                return OperationResult.elementNotFound().withMessage(ERR_NO_SUCH_TOKEN_FOUND);
            }

            //search token's authorization
            Authorization tokenAuthorization = token.get().getConfirmationFor();
            if (tokenAuthorization == null) {
                return OperationResult.generalFail().withMessage(ERR_TOKEN_HAS_NO_AUTHORIZATION);
            }

            // confirming authorization
            tokenAuthorization.setConfirmed(true);
            authorizationDao.save(tokenAuthorization);

            User user = token.get().getUser();
            Optional<Authorization> userLocalAuthorization =
                    authorizationDao.findByUserAndProvider(user, AuthProvider.LOCAL);
            if (userLocalAuthorization.isEmpty()) {
                log.error("{} User {} has no {} authorization. System Bug.",
                        TAG, user.getUsername(), AuthProvider.LOCAL.name());
                return OperationResult.generalFail().withMessage(ERR_USER_HAS_NO_LOCAL_AUTH);
            }

            //check local auth
            boolean localAccountConfirmed = userLocalAuthorization.get().isConfirmed();
            if (!localAccountConfirmed) {
                userLocalAuthorization.get().setConfirmed(true);
                authorizationDao.save(userLocalAuthorization.get());
            }
            return OperationResult.success();
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to confirm account got exception {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public Optional<Authorization> getAuthorization(User user, AuthProvider provider) {
        return authorizationDao.findByUserAndProvider(user, provider);
    }

    public Optional<String> decryptAuthorizationUser(Authorization authorization) {
        if (authorization != null && StringUtils.isNotBlank(authorization.getAuthUsername())) {
            OperationResult result = cryptTool.decrypt(authorization.getAuthUsername());
            if (result.ok()) {
                return Optional.of(result.getStringPayload());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
