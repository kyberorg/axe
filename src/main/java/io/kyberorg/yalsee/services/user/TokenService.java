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

@Slf4j
@AllArgsConstructor
@Service
public class TokenService {
    private static final String TAG = "[" + TokenService.class.getSimpleName() + "]";

    private static final String ERR_USER_ALREADY_HAS_TOKEN = "User already has token";

    private TokenDao tokenDao;

    public OperationResult createConfirmationToken(final User user, final Account authorization) {
        boolean userAlreadyHasConfirmationToken =
                tokenDao.existsByTokenTypeAndUser(TokenType.ACCOUNT_CONFIRMATION_TOKEN, user);
        if (userAlreadyHasConfirmationToken) {
            return OperationResult.banned().withMessage(ERR_USER_ALREADY_HAS_TOKEN);
        }

        Token confirmationToken = Token.create(TokenType.ACCOUNT_CONFIRMATION_TOKEN).forUser(user);
        confirmationToken.setConfirmationFor(authorization);

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

    public OperationResult createVerificationCode(User user) {
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

    public OperationResult createPasswordResetToken(User user) {
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

    public boolean isTokenExists(String token, TokenType tokenType) {
        Optional<Token> foundToken = tokenDao.findFirstByTokenAndTokenType(token, tokenType);
        if (foundToken.isPresent()) {
            Optional<Token> validToken = returnOnlyValidToken(foundToken.get());
            return validToken.isPresent();
        } else {
            return false;
        }
    }

    public Optional<Token> getToken(String tokenString) {
        Optional<Token> token = tokenDao.findFirstByToken(tokenString);
        return token.isPresent() ? returnOnlyValidToken(token.get()) : Optional.empty();
    }

    public OperationResult deleteToken(String token) {
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
