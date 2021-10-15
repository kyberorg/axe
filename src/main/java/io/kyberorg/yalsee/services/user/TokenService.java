package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.TokenDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.TokenType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class TokenService {
    private static final String TAG = "[" + TokenService.class.getSimpleName() + "]";

    private static final String ERR_USER_ALREADY_HAS_TOKEN = "User already has token";

    private TokenDao tokenDao;

    public OperationResult createConfirmationToken(final User user, final Authorization authorization) {
        boolean userAlreadyHasConfirmationToken =
                tokenDao.existsByTokenTypeAndUser(TokenType.ACCOUNT_CONFIRMATION_TOKEN, user);
        if (userAlreadyHasConfirmationToken) {
            return OperationResult.banned().withMessage(ERR_USER_ALREADY_HAS_TOKEN);
        }

        boolean tokenExist;
        String token;
        do {
            token = UUID.randomUUID().toString();
            tokenExist = tokenDao.existsByToken(token);
        } while (tokenExist);

        Token confirmationToken = new Token();
        confirmationToken.setToken(token);
        confirmationToken.setTokenType(TokenType.ACCOUNT_CONFIRMATION_TOKEN);
        confirmationToken.setUser(user);
        confirmationToken.setConfirmationFor(authorization);
        confirmationToken.setCreated(Timestamp.from(Instant.now()));
        confirmationToken.setUpdated(Timestamp.from(Instant.now()));

        try {
            tokenDao.save(confirmationToken);
            return OperationResult.success().addPayload(confirmationToken);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save token {} for user {}", TAG, token, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public OperationResult createVerificationCode(User user) {
        boolean codeExists;
        String code;
        do {
            code = RandomStringUtils.randomNumeric(6);
            codeExists = tokenDao.existsByToken(code);
        } while (codeExists);

        Token verificationCode = new Token();
        verificationCode.setToken(code);
        verificationCode.setTokenType(TokenType.LOGIN_VERIFICATION_TOKEN);
        verificationCode.setUser(user);
        verificationCode.setCreated(Timestamp.from(Instant.now()));
        verificationCode.setUpdated(Timestamp.from(Instant.now()));

        try {
            tokenDao.save(verificationCode);
            return OperationResult.success().addPayload(verificationCode);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save verification code {} for user {}", TAG, code, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public boolean isTokenExists(String token, TokenType tokenType) {
        return tokenDao.existsByTokenAndTokenType(token, tokenType);
    }

    public Optional<Token> getToken(String tokenString) {
        return tokenDao.findFirstByToken(tokenString);
    }

    public boolean isTokenExpired(String tokenString) {
        Optional<Token> token = getToken(tokenString);
        if (token.isEmpty()) {
            return true;
        } else {
            long created = token.get().getCreated().getTime();
            long expirationTime = created + (token.get().getTokenType().getTokenAge() * 1000L);
            return System.currentTimeMillis() > expirationTime;
        }
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
}
