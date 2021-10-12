package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.models.dao.UserDao;
import io.kyberorg.yalsee.result.OperationResult;
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
    public static final int PASSWORD_MIN_LENGTH = 3;

    public static final String OP_EMPTY_USERNAME = "Username cannot be empty";
    public static final String OP_USER_ALREADY_EXISTS = "Username already exists";
    public static final String OP_EMPTY_PASSWORD = "Password cannot be empty";
    public static final String OP_SHORT_PASSWORD = "Password is too short";
    public static final String ERR_NO_USER = "No such user";

    private final UserDao userDao;
    private final EncryptionUtils encryptionUtils;
    private final UserPreferencesService userPreferencesService;

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
            userPreferencesService.createEmptyPreferences(savedUser);
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
        if (isUserExists(username)) {
            return OperationResult.conflict().withMessage(OP_USER_ALREADY_EXISTS);
        }
        if (StringUtils.isBlank(plainPassword)) {
            return OperationResult.malformedInput().withMessage(OP_EMPTY_PASSWORD);
        }
        if (plainPassword.length() < PASSWORD_MIN_LENGTH) {
            return OperationResult.malformedInput().withMessage(OP_SHORT_PASSWORD);
        }
        return OperationResult.success();
    }

    public OperationResult passwordMatches(final String username, final String passwordCandidate) {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent()) {
            String encryptedPassword = constructPassword(passwordCandidate);
            boolean passwordMatches = encryptedPassword.equals(user.get().getPassword());
            return OperationResult.success().addPayload(passwordMatches);
        } else {
            return OperationResult.elementNotFound().withMessage(ERR_NO_USER);
        }
    }

    private String constructPassword(final String plainPassword) {
        final String serverSalt = encryptionUtils.getPasswordSalt();
        return plainPassword + serverSalt;
    }
}
