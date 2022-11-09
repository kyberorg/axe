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

/**
 * Service that operates with {@link UserPreferences}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserPreferencesService {
    public static final String TAG = "[" + UserPreferencesService.class.getSimpleName() + "]";

    private final UserPreferencesDao userPreferencesDao;

    /**
     * Creates new {@link UserPreferences}.
     *
     * @param user owner
     * @return {@link OperationResult} with created {@link UserPreferences} or {@link OperationResult} with error.
     */
    public OperationResult createNewPreferences(final User user) {
        UserPreferences userPreferences = UserPreferences.createForUser(user);
        return createOrUpdateUserPreferences(userPreferences);
    }

    /**
     * Provides {@link User}'s {@link UserPreferences}.
     *
     * @param user preferences owner
     * @return {@link Optional} with found {@link UserPreferences} or {@link Optional#empty()}
     */
    public Optional<UserPreferences> getUserPreferences(final User user) {
        return userPreferencesDao.findByUser(user);
    }

    /**
     * Update {@link UserPreferences}.
     *
     * @param userPreferences {@link UserPreferences} to update.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult updateUserPreferences(final UserPreferences userPreferences) {
        return createOrUpdateUserPreferences(userPreferences);
    }

    private OperationResult createOrUpdateUserPreferences(final UserPreferences userPreferences) {
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
