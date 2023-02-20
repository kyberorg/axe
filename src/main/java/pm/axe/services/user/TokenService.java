package pm.axe.services.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.db.dao.TokenDao;
import pm.axe.db.models.Account;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.users.TokenType;

import java.util.List;
import java.util.Optional;

/**
 * Service, that manipulates with {@link Token}s.
 */
@Slf4j
@AllArgsConstructor
@Service
public class TokenService {
    private static final String TAG = "[" + TokenService.class.getSimpleName() + "]";

    private final TokenDao tokenDao;

    /**
     * Creates {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN}.
     *
     * @param user    account owner.
     * @param account {@link Account} object
     * @return {@link OperationResult} with created {@link Token} or {@link OperationResult} with error.
     */
    public OperationResult createConfirmationToken(final User user, final Account account) {
        Optional<Token> optionalToken =
                tokenDao.findByTokenTypeAndUserAndConfirmationFor(TokenType.ACCOUNT_CONFIRMATION_TOKEN, user, account);
        Token confirmationToken;
        if (optionalToken.isPresent()) {
            //use it again
            confirmationToken = optionalToken.get();
        } else {
            //create new
            confirmationToken = Token.create(TokenType.ACCOUNT_CONFIRMATION_TOKEN).forUser(user);
            confirmationToken.setConfirmationFor(account);
            verifyTokenValueIsUnique(confirmationToken);
        }

        try {
            tokenDao.save(confirmationToken);
            return OperationResult.success().addPayload(confirmationToken);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save token {} for user {}", TAG, confirmationToken, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Creates {@link TokenType#TELEGRAM_CONFIRMATION_TOKEN}.
     *
     * @param user account owner.
     * @return {@link OperationResult} with created {@link Token} or {@link OperationResult} with error.
     */
    public OperationResult createTelegramConfirmationToken(final User user) {
        boolean userAlreadyHasTelegramConfirmationToken =
                tokenDao.existsByTokenTypeAndUser(TokenType.TELEGRAM_CONFIRMATION_TOKEN, user);
        if (userAlreadyHasTelegramConfirmationToken) {
            Token existingToken = tokenDao.findByTokenTypeAndUser(TokenType.TELEGRAM_CONFIRMATION_TOKEN, user);
            tokenDao.delete(existingToken);
        }

        Token token = Token.create(TokenType.TELEGRAM_CONFIRMATION_TOKEN).forUser(user);

        verifyTokenValueIsUnique(token);

        try {
            tokenDao.save(token);
            return OperationResult.success().addPayload(token);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to create {} token {} for user {}",
                    TAG, TokenType.TELEGRAM_CONFIRMATION_TOKEN, token, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Creates {@link TokenType#LOGIN_VERIFICATION_TOKEN}.
     *
     * @param user token's owner
     * @return {@link OperationResult} with created {@link Token} or {@link OperationResult} with error.
     */
    public OperationResult createVerificationCode(final User user) {
        Token verificationToken = Token.create(TokenType.LOGIN_VERIFICATION_TOKEN).forUser(user);
        verifyTokenValueIsUnique(verificationToken);

        try {
            tokenDao.save(verificationToken);
            return OperationResult.success().addPayload(verificationToken);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save verification token {} for user {}", TAG, verificationToken, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Create {@link TokenType#PASSWORD_RESET_TOKEN}.
     *
     * @param user token's owner
     * @return {@link OperationResult} with created {@link Token} or {@link OperationResult} with error.
     */
    public OperationResult createPasswordResetToken(final User user) {
        boolean tokenAlreadyExists = tokenDao.existsByTokenTypeAndUser(TokenType.PASSWORD_RESET_TOKEN, user);
        Token passwordResetToken;
        if (tokenAlreadyExists) {
            //update existing one
            passwordResetToken = tokenDao.findByTokenTypeAndUser(TokenType.PASSWORD_RESET_TOKEN, user);
            passwordResetToken.updateToken();
        } else {
            //creating new one
            passwordResetToken = Token.create(TokenType.PASSWORD_RESET_TOKEN).forUser(user);
        }

        verifyTokenValueIsUnique(passwordResetToken);

        try {
            tokenDao.update(passwordResetToken);
            return OperationResult.success().addPayload(passwordResetToken);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save {} for user {}", TAG, TokenType.PASSWORD_RESET_TOKEN.name(), user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Check if {@link Token} exists.
     *
     * @param token     string with token value.
     * @param tokenType type of {@link Token}
     * @return true if token exists, false - is not.
     */
    public boolean isTokenExists(final String token, final TokenType tokenType) {
        Optional<Token> foundToken = tokenDao.findFirstByTokenAndTokenType(token, tokenType);
        if (foundToken.isPresent()) {
            Optional<Token> validToken = returnOnlyValidToken(foundToken.get());
            return validToken.isPresent();
        } else {
            return false;
        }
    }

    /**
     * Gets Token by its {@link Token#token} value.
     *
     * @param tokenString string with token value
     * @return {@link Optional} with found {@link Token} found or {@link Optional#empty()}
     */
    public Optional<Token> getToken(final String tokenString) {
        Optional<Token> token = tokenDao.findFirstByToken(tokenString);
        return token.isPresent() ? returnOnlyValidToken(token.get()) : Optional.empty();
    }

    /**
     * Gets Token by {@link User} and {@link TokenType}.
     *
     * @param user {@link Token}'s owner
     * @param tokenType {@link Token}'s type
     *
     * @return {@link Optional} with valid {@link Token} or {@link Optional#empty()}
     * @throws IllegalArgumentException when user or token typer params are null
     */
    public Optional<Token> getToken(final User user, final TokenType tokenType) {
        if (user == null) throw new IllegalArgumentException("user cannot be null");
        if (tokenType == null) throw new IllegalArgumentException("token type cannot be null");

        Token token = tokenDao.findByTokenTypeAndUser(tokenType, user);
        return token != null ? returnOnlyValidToken(token) : Optional.empty();
    }

    /**
     * Provides {@link User}'s {@link TokenType#TELEGRAM_CONFIRMATION_TOKEN} token.
     *
     * @param user token's owner
     * @return {@link Optional} with {@link Token} or {@link Optional#empty()}.
     */
    public Optional<Token> getTelegramToken(final User user) {
        return Optional.ofNullable(tokenDao.findByTokenTypeAndUser(TokenType.TELEGRAM_CONFIRMATION_TOKEN, user));
    }

    /**
     * Deletes token.
     *
     * @param token string with token
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult deleteToken(final String token) {
        try {
            Optional<Token> tokenRecord = tokenDao.findFirstByToken(token);
            if (tokenRecord.isPresent()) {
                tokenDao.delete(tokenRecord.get());
                return OperationResult.success();
            } else {
                log.warn("{} unable to delete token {}. Reason: token not found", TAG, token);
                return OperationResult.elementNotFound();
            }
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Deletes {@link Token} record in {@link Async} manner.
     *
     * @param token record to delete.
     */
    @Async
    public void deleteTokenRecord(final Token token) {
        try {
            tokenDao.delete(token);
            log.info("{} {} deleted successfully.", TAG, token);
        } catch (CannotCreateTransactionException c) {
            log.error("{} unable to delete token. DB is DOWN", TAG);
        } catch (Exception e) {
            log.error("{} unable to delete token. Got exception {}", TAG, e.getMessage());
        }
    }

    /**
     * Gets all {@link Token}s owned by given {@link User}. This method ain't check if {@link Token} is valid or not.
     *
     * @param user {@link Token}'s owner.
     * @return list of {@link Token}s owned by given {@link User}.
     */
    public List<Token> getAllTokensOwnedByUser(final User user) {
        return tokenDao.findByUser(user);
    }

    private void verifyTokenValueIsUnique(final Token token) {
        boolean tokenValueAlreadySet = isTokenValueAlreadyExists(token.getToken());
        if (!tokenValueAlreadySet) return;
        do {
            token.updateToken();
        } while (isTokenValueAlreadyExists(token.getToken()));
    }

    private boolean isTokenValueAlreadyExists(final String token) {
        return tokenDao.existsByToken(token);
    }

    private Optional<Token> returnOnlyValidToken(final Token token) {
        if (token.isStillValid()) {
            return Optional.of(token);
        } else {
            tokenDao.deleteExpiredToken(token);
            return Optional.empty();
        }
    }
}
