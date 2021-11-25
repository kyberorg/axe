package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Login;
import io.kyberorg.yalsee.models.dao.base.TimeAwareCrudRepository;

import java.util.Optional;

public interface LoginDao extends TimeAwareCrudRepository<Login, Long> {
    Optional<Login> findBySarja(String sarja);

    boolean existsBySarja(String sarja);

    boolean existsByToken(String token);
}
