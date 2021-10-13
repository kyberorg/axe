package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.TokenType;
import org.springframework.data.repository.CrudRepository;

public interface TokenDao extends CrudRepository<Token, Long> {
    boolean existsByTokenTypeAndUser(TokenType tokenType, User user);

    boolean existsByToken(String token);


}