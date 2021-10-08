package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.UserPreferences;
import org.springframework.data.repository.CrudRepository;

public interface UserPreferencesDao extends CrudRepository<UserPreferences, Long> {
}