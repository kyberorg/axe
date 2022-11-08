package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.UserDao;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.UsernameValidator;
import io.kyberorg.yalsee.utils.crypto.PasswordUtils;
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

    private final UserDao userDao;
    private final PasswordUtils passwordUtils;

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

    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }

    public OperationResult validateUsername(String username) {
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

    public OperationResult createUser(final String username, final String plainPassword) {
        log.info("{} Got create user request: username {}", TAG, username);
        OperationResult usernameValidationResult = validateUsername(username);
        if (usernameValidationResult.notOk()) return usernameValidationResult;

        OperationResult passwordValidationResult = validatePassword(plainPassword);
        if (passwordValidationResult.notOk()) return passwordValidationResult;

        try {
            String encryptedPassword = passwordUtils.encryptPassword(plainPassword);
            User newUser = User.create(username, encryptedPassword);
            User savedUser = userDao.save(newUser);
            log.info("{} User saved. Username: {}", TAG, newUser.getUsername());
            return OperationResult.success().addPayload(savedUser);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, User.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    public boolean checkPassword(final User user, final String passwordCandidate) {
        String storedPassword = user.getPassword();
        return passwordUtils.passwordMatches(storedPassword, passwordCandidate);
    }

    public OperationResult resetPassword(User user, String newPassword) {
        try {
            user.setPassword(passwordUtils.encryptPassword(newPassword));
            userDao.update(user);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on resetting password for {} {}",
                    TAG, User.class.getSimpleName(), user.getUsername());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    void enableUser(final User user) {
        user.setEnabled(true);
        userDao.update(user);
    }
}
