package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserPreferences;
import io.kyberorg.yalsee.models.dao.UserPreferencesDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserPreferencesService {
    private final UserPreferencesDao userPreferencesDao;

    public void createEmptyPreferences(final User user) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUser(user);
        userPreferencesDao.save(userPreferences);
    }
}
