package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.dao.base.TimeAwareCrudDao;
import io.kyberorg.yalsee.models.User;

import java.util.Optional;

public interface UserDao extends TimeAwareCrudDao<User, Long> {
    Optional<User> findByUsername(String username);

}