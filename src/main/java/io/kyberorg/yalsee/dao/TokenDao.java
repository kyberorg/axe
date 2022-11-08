package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.dao.base.TimeAwareCrudDao;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.TokenType;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public interface TokenDao extends TimeAwareCrudDao<Token, Long> {

    boolean existsByTokenTypeAndUser(TokenType tokenType, User user);

    boolean existsByToken(String token);

    Optional<Token> findFirstByTokenAndTokenType(String token, TokenType tokenType);

    Optional<Token> findFirstByToken(String token);

    Token findByTokenTypeAndUser(TokenType tokenType, User user);

    /**
     * Async Token deletion.
     *
     * @param token token record to delete
     */
    @Async
    default void deleteExpiredToken(@NonNull final Token token) {
        delete(token);
    }
}
