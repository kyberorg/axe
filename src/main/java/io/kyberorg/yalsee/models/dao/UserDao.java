package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

}