package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserSettings;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * DAO for {@link UserSettings} table.
 */
public interface UserSettingsDao extends CrudRepository<UserSettings, Long> {
    /**
     * Find {@link UserSettings} for given {@link User}.
     *
     * @param user owner of {@link UserSettings}
     * @return {@link Optional} with found {@link UserSettings} record or {@link Optional#empty()}.
     */
    Optional<UserSettings> findByUser(User user);
}
