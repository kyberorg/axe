package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.models.dao.UserPreferencesDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserPreferencesService {
    private final UserPreferencesDao userPreferencesDao;

    public void createEmptyPreferences(final User user) {
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
}
