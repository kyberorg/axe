package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.TokenDao;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.TokenType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Optional;

/**
 * Service, that manipulates with {@link Token}s.
 */
@Slf4j
@AllArgsConstructor
@Service
public class TokenService {
    private static final String TAG = "[" + TokenService.class.getSimpleName() + "]";

    private static final String ERR_USER_ALREADY_HAS_TOKEN = "User already has token";

    private final TokenDao tokenDao;

    /**
     * Creates {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN}.
     *
     * @param user    account owner.
     * @param account {@link Account} object
     * @return {@link OperationResult} with created {@link Token} or {@link OperationResult} with error.
     */
    public OperationResult createConfirmationToken(final User user, final Account account) {
        boolean userAlreadyHasConfirmationToken =
                tokenDao.existsByTokenTypeAndUser(TokenType.ACCOUNT_CONFIRMATION_TOKEN, user);
        if (userAlreadyHasConfirmationToken) {
            return OperationResult.banned().withMessage(ERR_USER_ALREADY_HAS_TOKEN);
        }

        Token confirmationToken = Token.create(TokenType.ACCOUNT_CONFIRMATION_TOKEN).forUser(user);
        confirmationToken.setConfirmationFor(account);

        verifyTokenValueIsUnique(confirmationToken);

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
     * Deletes token.
     *
     * @param token string with token
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult deleteToken(final String token) {
        try {
            Optional<Token> tokenRecord = getToken(token);
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