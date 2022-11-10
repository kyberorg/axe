package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.UserSettingsDao;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserSettings;
import io.kyberorg.yalsee.result.OperationResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

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

    private OperationResult createOrUpdateUserSettings(final UserSettings userSettings) {
        try {
            userSettingsDao.save(userSettings);
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
