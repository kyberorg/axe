package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.UserPreferencesDao;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.result.OperationResult;
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

    public OperationResult createNewPreferences(final User user) {
        UserPreferences userPreferences = UserPreferences.createForUser(user);
        return createOrUpdateUserPreferences(userPreferences);
    }

    public Optional<UserPreferences> getUserPreferences(final User user) {
        return userPreferencesDao.findByUser(user);
    }

    public OperationResult updateUserPreferences(final UserPreferences userPreferences) {
        return createOrUpdateUserPreferences(userPreferences);
    }

    private OperationResult createOrUpdateUserPreferences(UserPreferences userPreferences) {
        try {
            userPreferencesDao.save(userPreferences);
            return OperationResult.success().addPayload(userPreferences);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing {}", TAG, User.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }
}
