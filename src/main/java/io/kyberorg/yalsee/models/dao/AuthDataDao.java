package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.AuthData;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.AuthProvider;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthDataDao extends CrudRepository<AuthData, Long> {
    Optional<AuthData> findByUserAndAuthProvider(User user, AuthProvider authProvider);

}