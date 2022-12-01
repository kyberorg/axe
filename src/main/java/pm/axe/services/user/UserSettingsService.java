package pm.axe.services.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.db.dao.UserSettingsDao;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.result.OperationResult;

import java.util.Optional;

/**
 * Service that operates with {@link UserSettings}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserSettingsService {
    public static final String TAG = "[" + UserSettingsService.class.getSimpleName() + "]";

    private final UserSettingsDao userSettingsDao;

    /**
     * Creates new {@link UserSettings}.
     *
     * @param user owner
     * @return {@link OperationResult} with created {@link UserSettings} or {@link OperationResult} with error.
     */
    public OperationResult createNewSettings(final User user) {
        UserSettings userSettings = UserSettings.createForUser(user);
        return createOrUpdateUserSettings(userSettings);
    }

    /**
     * Provides {@link User}'s {@link UserSettings}.
     *
     * @param user settings owner
     * @return {@link Optional} with found {@link UserSettings} or {@link Optional#empty()}
     */
    public Optional<UserSettings> getUserSettings(final User user) {
        return userSettingsDao.findByUser(user);
    }

    /**
     * Update {@link UserSettings}.
     *
     * @param userSettings {@link UserSettings} to update.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult updateUserSettings(final UserSettings userSettings) {
        return createOrUpdateUserSettings(userSettings);
    }

    /**
     * Deletes {@link UserSettings}.
     *
     * @param settings {@link UserSettings} to delete.
     */
    public void deleteUserSettings(final UserSettings settings) {
        userSettingsDao.delete(settings);
    }

    private OperationResult createOrUpdateUserSettings(final UserSettings userSettings) {
        try {
            userSettingsDao.save(userSettings);
            log.info("{} Saved/Updated {} for {} {}",
                    TAG, UserSettings.class.getSimpleName(),
                    User.class.getSimpleName(), userSettings.getUser().getUsername());
            return OperationResult.success().addPayload(userSettings);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on storing {}", TAG, UserSettings.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }
}
