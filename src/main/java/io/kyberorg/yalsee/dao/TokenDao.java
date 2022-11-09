package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.dao.base.TimeAwareCrudDao;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.TokenType;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

/**
 * DAO for {@link Token} table.
 */
public interface TokenDao extends TimeAwareCrudDao<Token, Long> {

    /**
     * Controls if {@link Token} record of given {@link TokenType} and owned by {@link User} exists.
     *
     * @param tokenType {@link TokenType} wanted.
     * @param user      {@link Token} owner
     * @return true - if record exists, false - if not.
     */
    boolean existsByTokenTypeAndUser(final TokenType tokenType, final User user);

    /**
     * Controls if {@link Token} with given {@link Token#token} aka Token Value.
     *
     * @param token string with token value.
     * @return true - if record found, false - if not.
     */
    boolean existsByToken(final String token);

    /**
     * Finds {@link Token} record by {@link Token#token} and {@link TokenType}.
     *
     * @param token     string with Token value
     * @param tokenType Type of token
     * @return {@link Optional} with first {@link Token} found (since this combination designed to be unique)
     * or {@link Optional#empty()}
     */
    Optional<Token> findFirstByTokenAndTokenType(final String token, final TokenType tokenType);

    /**
     * Finds {@link Token} record by {@link Token#token} value.
     *
     * @param token string with Token value
     * @return {@link Optional} with first {@link Token} found (since this token value designed to be unique)
     * * or {@link Optional#empty()}
     */
    Optional<Token> findFirstByToken(final String token);

    /**
     * Finds {@link Token} record by {@link TokenType} and {@link User}, who owns given {@link Token}.
     * Please note that given method can return {@code null},
     * so consider to control record existence first by using {@link #existsByTokenTypeAndUser(TokenType, User)}.
     *
     * @param tokenType type of token
     * @param user      {@link Token}'s owner
     * @return found {@link Token} record or {@code null}
     */
    Token findByTokenTypeAndUser(final TokenType tokenType, final User user);

    /**
     * Async Token deletion.
     *
     * @param token {@link Token} record to delete
     */
    @Async
    default void deleteExpiredToken(@NonNull final Token token) {
        delete(token);
    }
}
