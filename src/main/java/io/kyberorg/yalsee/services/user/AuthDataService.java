package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.dao.AuthDataDao;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthDataService {
    private final AuthDataDao authDataDao;

    public boolean isEmailAlreadyUsed(final String email) {
        if (StringUtils.isBlank(email)) return false;
        return authDataDao.existsByAuthProviderAndAuthUsername(AuthProvider.EMAIL, email);
    }
}
