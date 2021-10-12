package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.AuthProvider;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorizationDao extends CrudRepository<Authorization, Long> {
    Optional<Authorization> findByUserAndProvider(User user, AuthProvider authProvider);

    List<Authorization> findByProvider(AuthProvider provider);

}