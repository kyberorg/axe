package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.models.dao.UserPreferencesDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserPreferencesService {
    public static final String TAG = "[" + UserPreferencesService.class.getSimpleName() + "]";

    private final UserPreferencesDao userPreferencesDao;

    public void createNewPreferences(final User user) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUser(user);
        userPreferencesDao.save(userPreferences);
    }

    public OperationResult setTwoFactorChannel(User user, AuthProvider channel, boolean enabled) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            UserPreferences up = userPreferences.get();
            up.setTfaChannel(channel);
            up.setTfaEnabled(enabled);
            try {
                userPreferencesDao.save(up);
                return OperationResult.success();
            } catch (CannotCreateTransactionException c) {
                return OperationResult.databaseDown();
            } catch (Exception e) {
                return OperationResult.generalFail();
            }
        } else {
            return OperationResult.elementNotFound();
        }
    }

    public boolean isTfaEnabled(final User user) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            return userPreferences.get().isTfaEnabled();
        } else {
            log.error("{} user {} has no {}", TAG, user, UserPreferences.class.getSimpleName());
            return false;
        }
    }

    public AuthProvider getTfaChannel(final User user) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            return userPreferences.get().getTfaChannel();
        } else {
            log.error("{} user {} has no {}", TAG, user, UserPreferences.class.getSimpleName());
            return null;
        }
    }

    public OperationResult updateMainChannel(User user, AuthProvider mainChannel) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            try {
                userPreferences.get().setMainChannel(mainChannel);
                userPreferencesDao.save(userPreferences.get());
                return OperationResult.success();
            } catch (CannotCreateTransactionException c) {
                return OperationResult.databaseDown();
            } catch (Exception e) {
                return OperationResult.generalFail();
            }
        } else {
            log.error("{} user {} has no {}", TAG, user, UserPreferences.class.getSimpleName());
            return null;
        }
    }

    public AuthProvider getPasswordResetChannel(User user) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            return userPreferences.get().getPasswordResetChannel();
        } else {
            log.error("{} user {} has no {}", TAG, user, UserPreferences.class.getSimpleName());
            return null;
        }
    }

    public AuthProvider getMainChannel(User user) {
        Optional<UserPreferences> userPreferences = userPreferencesDao.findByUser(user);
        if (userPreferences.isPresent()) {
            return userPreferences.get().getMainChannel();
        } else {
            log.error("{} user {} has no {}", TAG, user, UserPreferences.class.getSimpleName());
            return null;
        }
    }
}
