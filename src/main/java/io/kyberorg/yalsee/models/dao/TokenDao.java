package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.base.TimeRepository;
import io.kyberorg.yalsee.users.TokenType;

import java.util.Optional;

public interface TokenDao extends TimeRepository<Token, Long> {
    boolean existsByTokenTypeAndUser(TokenType tokenType, User user);

    boolean existsByToken(String token);

    boolean existsByTokenAndTokenType(String token, TokenType tokenType);

    Optional<Token> findFirstByToken(String token);

    Token findByTokenTypeAndUser(TokenType tokenType, User user);

}