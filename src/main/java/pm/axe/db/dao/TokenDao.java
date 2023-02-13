package pm.axe.db.dao;

import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;
import pm.axe.db.dao.base.TimeAwareCrudDao;
import pm.axe.db.models.Account;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.users.TokenType;

import java.util.List;
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
    boolean existsByTokenTypeAndUser(TokenType tokenType, User user);

    /**
     * Controls if {@link Token} with given {@link Token#token} aka Token Value.
     *
     * @param token string with token value.
     * @return true - if record found, false - if not.
     */
    boolean existsByToken(String token);

    /**
     * Finds {@link Token} record by {@link Token#token} and {@link TokenType}.
     *
     * @param token     string with Token value
     * @param tokenType Type of token
     * @return {@link Optional} with first {@link Token} found (since this combination designed to be unique)
     * or {@link Optional#empty()}
     */
    Optional<Token> findFirstByTokenAndTokenType(String token, TokenType tokenType);

    /**
     * Finds {@link Token} record by {@link Token#token} value.
     *
     * @param token string with Token value
     * @return {@link Optional} with first {@link Token} found (since this token value designed to be unique)
     * * or {@link Optional#empty()}
     */
    Optional<Token> findFirstByToken(String token);

    /**
     * Finds {@link Token} record by {@link TokenType} and {@link User}, who owns given {@link Token}.
     * Please note that given method can return {@code null},
     * so consider to control record existence first by using {@link #existsByTokenTypeAndUser(TokenType, User)}.
     *
     * @param tokenType type of token
     * @param user      {@link Token}'s owner
     * @return found {@link Token} record or {@code null}
     */
    Token findByTokenTypeAndUser(TokenType tokenType, User user);

    /**
     * Finds {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN} by its {@link TokenType}, {@link User} and {@link Account}.
     *
     * @param tokenType normally {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN}.
     * @param user {@link Token}'s owner
     * @param account {@link Account} to be confirmed
     *
     * @return {@link Optional} with found {@link Token} record or {@link Optional#empty()}.
     */
    Optional<Token> findByTokenTypeAndUserAndConfirmationFor(TokenType tokenType, User user, Account account);

    /**
     * Finds all {@link Token} records owned by {@link User}.
     *
     * @param user {@link Token}'s owner
     * @return list of found {@link Token}s or empty {@link List}.
     */
    List<Token> findByUser(User user);

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
