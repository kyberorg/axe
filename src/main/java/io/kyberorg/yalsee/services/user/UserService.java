package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.core.UsernameValidator;
import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.models.dao.UserDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.passresetsenders.PassResetSenders;
import io.kyberorg.yalsee.users.AuthProvider;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private static final String TAG = "[" + UserService.class.getSimpleName() + "]";
    public static final int USERNAME_MAX_LENGTH = 100;
    public static final int PASSWORD_MIN_LENGTH = 3;

    public static final String OP_EMPTY_USERNAME = "Username cannot be empty";
    public static final String OP_USER_ALREADY_EXISTS = "Username already exists";
    public static final String OP_EMPTY_PASSWORD = "Password cannot be empty";
    public static final String OP_SHORT_PASSWORD = "Password is too short";
    public static final String ERR_NOT_VALID_CHARS_IN_USERNAME = "There are non-valid chars in username";
    private static final String ERR_USERNAME_IS_TOO_LONG = "Username is too long";
    private static final String ERR_NO_PASSWORD_RESET_CHANNEL = "User has no password reset channel";
    private static final String ERR_PASSWORD_RESET_CHANNEL_NOT_FOUND = "Password Reset Channel no longer exists";
    private static final String ERR_PASSWORD_RESET_CHANNEL_UNCONFIRMED = "Password Reset Channel unconfirmed";
    private static final String ERR_PASSWORD_RESET_CHANNEL_DECRYPTION_FAIL = "Failed to decrypt Password Reset Channel";
    private static final String ERR_PASSWORD_RESET_TOKEN_GENERATION_FAIL = "Failed to generate Password Reset Token";

    private final UserDao userDao;
    private final EncryptionUtils encryptionUtils;
    private final UserPreferencesService userPreferencesService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final PassResetSenders passResetSenders;

    public boolean isUserExists(final String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<User> optionalUser = userDao.findByUsername(username);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UsernameNotFoundException(
                    MessageFormat.format("User with username {0} cannot be found.", username));
        }
    }

    public OperationResult createUser(final String username, final String plainPassword) {
        log.info("{} Got create user request: username {}", TAG, username);
        OperationResult validationResult = validateParams(username, plainPassword);
        if (validationResult.notOk()) {
            return validationResult;
        }
        //Action
        try {
            String encryptedPassword = encryptionUtils.getPasswordEncoder().encode(constructPassword(plainPassword));
            User newUser = User.create(username, encryptedPassword);
            User savedUser = userDao.save(newUser);
            log.info("{} User saved. Username: {}", TAG, newUser.getUsername());

            //create user prefs
            userPreferencesService.createNewPreferences(savedUser);
            log.info("{} created {} for '{}' user", TAG, UserPreferences.class.getSimpleName(), username);
            return OperationResult.success().addPayload(savedUser);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, User.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    public OperationResult validateParams(String username, String plainPassword) {
        if (StringUtils.isBlank(username)) {
            return OperationResult.malformedInput().withMessage(OP_EMPTY_USERNAME);
        }
        if (!UsernameValidator.isValid(username)) {
            return OperationResult.malformedInput().withMessage(ERR_NOT_VALID_CHARS_IN_USERNAME);
        }
        if (username.length() > USERNAME_MAX_LENGTH) {
            return OperationResult.malformedInput().withMessage(ERR_USERNAME_IS_TOO_LONG);
        }
        if (isUserExists(username)) {
            return OperationResult.conflict().withMessage(OP_USER_ALREADY_EXISTS);
        }
        OperationResult passwordValidationResult = validatePassword(plainPassword);
        if (passwordValidationResult.notOk()) {
            return passwordValidationResult;
        }

        return OperationResult.success();
    }

    public OperationResult validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            return OperationResult.malformedInput().withMessage(OP_EMPTY_PASSWORD);
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            return OperationResult.malformedInput().withMessage(OP_SHORT_PASSWORD);
        }
        return OperationResult.success();
    }

    public boolean checkPassword(final User user, final String passwordCandidate) {
        String rawPassword = constructPassword(passwordCandidate);
        String storedPassword = user.getPassword();
        return encryptionUtils.getPasswordEncoder().matches(rawPassword, storedPassword);
    }

    private String constructPassword(final String plainPassword) {
        final String serverSalt = encryptionUtils.getPasswordSalt();
        return plainPassword + serverSalt;
    }

    public OperationResult sendPasswordResetLink(final User user) {
        AuthProvider passwordResetChannel = userPreferencesService.getPasswordResetChannel(user);
        if (passwordResetChannel == null) {
            passwordResetChannel = userPreferencesService.getMainChannel(user);
        }

        if (passwordResetChannel == null || passwordResetChannel == AuthProvider.LOCAL) {
            return OperationResult.generalFail().withMessage(ERR_NO_PASSWORD_RESET_CHANNEL);
        }

        Optional<Authorization> authorization = authService.getAuthorization(user, passwordResetChannel);
        if (authorization.isEmpty()) {
            return OperationResult.generalFail().withMessage(ERR_PASSWORD_RESET_CHANNEL_NOT_FOUND);
        }

        if (!authorization.get().isConfirmed()) {
            return OperationResult.generalFail().withMessage(ERR_PASSWORD_RESET_CHANNEL_UNCONFIRMED);
        }

        Optional<String> destination = authService.decryptAuthorizationUser(authorization.get());
        if (destination.isEmpty()) {
            return OperationResult.generalFail().withMessage(ERR_PASSWORD_RESET_CHANNEL_DECRYPTION_FAIL);
        }

        OperationResult getTokenResult = tokenService.createPasswordResetToken(user);
        if (getTokenResult.notOk()) {
            return OperationResult.generalFail().withMessage(ERR_PASSWORD_RESET_TOKEN_GENERATION_FAIL);
        }

        String passwordResetToken = getTokenResult.getPayload(Token.class).getToken();

        return passResetSenders.get(passwordResetChannel).sendResetLink(destination.get(), passwordResetToken);
    }

    public boolean nowhereToSend(String result) {
        return result.equals(ERR_NO_PASSWORD_RESET_CHANNEL) || result.equals(ERR_PASSWORD_RESET_CHANNEL_UNCONFIRMED);
    }

    public OperationResult resetPassword(User user, String newPassword) {
        try {
            user.setPassword(encryptionUtils.getPasswordEncoder().encode(constructPassword(newPassword)));
            userDao.update(user);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, User.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }
}