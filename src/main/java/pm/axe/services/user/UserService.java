package pm.axe.services.user;

import pm.axe.db.dao.UserDao;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.users.PasswordValidator;
import pm.axe.users.UsernameValidator;
import pm.axe.utils.crypto.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * Service, that operates with {@link User}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private static final String TAG = "[" + UserService.class.getSimpleName() + "]";
    private static final String ERR_USER_ALREADY_EXISTS = "Username already exists";
    private final UserDao userDao;
    private final PasswordUtils passwordUtils;

    /**
     * Controls if {@link User} exits by its username.
     *
     * @param username string with username.
     * @return true - if {@link User} exists, false - if not
     */
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

    /**
     * Creates {@link User} .
     *
     * @param username      string with username
     * @param plainPassword string with plain password
     * @return {@link OperationResult} with created {@link User} in payload or {@link OperationResult} with error.
     */
    public OperationResult createUser(final String username, final String plainPassword) {
        OperationResult usernameValidationResult = UsernameValidator.isValid(username);
        if (usernameValidationResult.notOk()) return usernameValidationResult;

        if (isUserExists(username)) {
            return OperationResult.conflict().withMessage(ERR_USER_ALREADY_EXISTS);
        }

        OperationResult passwordValidationResult = PasswordValidator.isPasswordValid(plainPassword);
        if (passwordValidationResult.notOk()) return passwordValidationResult;

        try {
            String encryptedPassword = passwordUtils.encryptPassword(plainPassword);
            User newUser = User.create(username, encryptedPassword);
            User savedUser = userDao.save(newUser);
            log.info("{} Saved User. Username: {}", TAG, newUser.getUsername());
            return OperationResult.success().addPayload(savedUser);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, User.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    /**
     * Checks Password.
     *
     * @param user              password's owner
     * @param passwordCandidate string with plain password candidate.
     * @return true if passwords are same, false - if not.
     */
    public boolean checkPassword(final User user, final String passwordCandidate) {
        String storedPassword = user.getPassword();
        return passwordUtils.passwordMatches(storedPassword, passwordCandidate);
    }

    /**
     * Reset password.
     *
     * @param user        password's owner
     * @param newPassword string with plain password known as new password.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error and message.
     */
    public OperationResult resetPassword(final User user, final String newPassword) {
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

    /**
     * Returns User by its ID.
     *
     * @param id id of {@link User}
     * @return {@link Optional} with found {@link User} or {@link Optional#empty()}
     */
    public Optional<User> getUserById(final long id) {
        return userDao.findById(id);
    }

    /**
     * Method, that returns default application user.
     *
     * @return {@link User} record for default user.
     * @throws UsernameNotFoundException when no default user found.
     */
    public User getDefaultUser() throws UsernameNotFoundException {
        //returning Axe user
        Optional<User> axe = getUserById(1L);
        if (axe.isPresent()) {
            return axe.get();
        } else {
            throw new UsernameNotFoundException("Suddenly there is no default user in the system");
        }
    }

    void confirmUser(final User user) {
        user.setConfirmed(true);
        userDao.update(user);
    }


}
