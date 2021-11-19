package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.base.TimeRepository;

import java.util.Optional;

public interface UserDao extends TimeRepository<User, Long> {
    Optional<User> findByUsername(String username);

}