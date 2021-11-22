package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.base.TimeAwareCrudRepository;

import java.util.Optional;

public interface UserDao extends TimeAwareCrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

}