package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserPreferencesDao extends CrudRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUser(User user);

}