package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.TokenDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.users.TokenType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class TokenService {
    private static final String TAG = "[" + TokenService.class.getSimpleName() + "]";

    private static final String ERR_USER_ALREADY_HAS_TOKEN = "User already has token";

    private TokenDao tokenDao;

    public OperationResult createConfirmationToken(final User user) {
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
}
