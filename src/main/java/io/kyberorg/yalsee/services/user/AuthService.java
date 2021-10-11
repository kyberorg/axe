package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.dao.AuthorizationDao;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final AuthorizationDao authorizationDao;

    public boolean isEmailAlreadyUsed(final String email) {
        if (StringUtils.isBlank(email)) return false;
        return authorizationDao.existsByProviderAndAuthUsername(AuthProvider.EMAIL, email);
    }
}
