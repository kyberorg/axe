package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * DAO for {@link UserPreferences} table.
 */
public interface UserPreferencesDao extends CrudRepository<UserPreferences, Long> {
    /**
     * Find {@link UserPreferences} for given {@link User}.
     *
     * @param user owner of {@link UserPreferences}
     * @return {@link Optional} with found {@link UserPreferences} record or {@link Optional#empty()}.
     */
    Optional<UserPreferences> findByUser(User user);
}
