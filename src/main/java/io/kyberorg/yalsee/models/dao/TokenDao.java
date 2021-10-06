package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.TokenType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenDao extends CrudRepository<Token, Long> {
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);

}