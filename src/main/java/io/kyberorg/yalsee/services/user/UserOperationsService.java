package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.internal.RegisterUserInput;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserSettings;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.senders.Senders;
import io.kyberorg.yalsee.services.user.rollback.RollbackService;
import io.kyberorg.yalsee.services.user.rollback.RollbackTask;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.users.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Stack;

/**
 * Service, that performs user-related operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserOperationsService {
    private static final String TAG = "[" + UserOperationsService.class.getSimpleName() + "]";
    public static final String TELEGRAM_TOKEN_KEY = "telegramToken";
    private final Stack<RollbackTask> rollbackTasks = new Stack<>();
    private final RollbackService rollbackService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final Senders senders;

    /**
     * Registers new User in System.
     * Creates new records in App's Database and
     * requests {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN} from {@link TokenService}.
     *
     * @param input {@link RegisterUserInput} with filled in fields.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error status and message inside.
     */
    public OperationResult registerUser(final RegisterUserInput input) {
        //Create User Record
        OperationResult userCreateResult = userService.createUser(input.getUsername(), input.getPassword());
        if (userCreateResult.notOk()) {
            return userCreateResult;
        }
        User createdUser = userCreateResult.getPayload(User.class);
        rollbackTasks.push(RollbackTask.create(User.class, createdUser));

        //Create UserSettings Record
        OperationResult userSettingsCreateResult = userSettingsService.createNewSettings(createdUser);
        if (userSettingsCreateResult.notOk()) {
            rollbackService.rollback(rollbackTasks);
            return userSettingsCreateResult;
        }
        UserSettings userSettings = userSettingsCreateResult.getPayload(UserSettings.class);
        rollbackTasks.push(RollbackTask.create(UserSettings.class, userSettings));

        Account userAccount;
        if (StringUtils.isBlank(input.getEmail())) {
            //Create Local Account
            OperationResult createLocalAccountResult = accountService.createLocalAccount(createdUser);
            if (createLocalAccountResult.notOk()) {
                log.error("{} Failed to create local {}. OpResult: {}",
                        TAG, Account.class.getSimpleName(), createLocalAccountResult);
                rollbackService.rollback(rollbackTasks);
                return createLocalAccountResult;
            }
            Account localAccount = createLocalAccountResult.getPayload(Account.class);
            userAccount = localAccount;
            rollbackTasks.push(RollbackTask.create(Account.class, localAccount));
        } else {
            //Create Email Account
            OperationResult createEmailAccountResult = accountService.createEmailAccount(createdUser, input.getEmail());
            if (createEmailAccountResult.notOk()) {
                log.error("{} Failed to create email {}. OpResult: {}",
                        TAG, Account.class.getSimpleName(), createEmailAccountResult);
                rollbackService.rollback(rollbackTasks);
                return createEmailAccountResult;
            }
            Account emailAccount = createEmailAccountResult.getPayload(Account.class);
            userAccount = emailAccount;
            rollbackTasks.push(RollbackTask.create(Account.class, emailAccount));
        }
        //UserSettings change Main Channel to Email
        userSettings.setMainChannel(userAccount.getType());

        //if TFA (2-factor Auth) enabled
        if (input.isTfaEnabled() && userAccount.getType() == AccountType.EMAIL) {
            //Set TFA enabled and update its channel to Email
            userSettings.setTfaEnabled(true);
            userSettings.setTfaChannel(AccountType.EMAIL);
        }
        //Save UserSettings
        OperationResult saveChannelUpdatesResult = userSettingsService.updateUserSettings(userSettings);
        if (saveChannelUpdatesResult.notOk()) {
            log.error("{} Failed to update {}. OpResult: {}",
                    TAG, UserSettings.class.getSimpleName(), saveChannelUpdatesResult);
        }
        //Create and send - confirmation email for Accounts with Email set.
        if (userAccount.getType() == AccountType.EMAIL) {
            //Create Confirmation Token
            OperationResult createConfirmationTokenResult =
                    tokenService.createConfirmationToken(createdUser, userAccount);
            if (createConfirmationTokenResult.notOk()) {
                log.error("{} failed to create confirmation token for {}. OpResult: {}",
                        TAG, createdUser.getUsername(), createConfirmationTokenResult);
                return createConfirmationTokenResult;
            }
            Token confirmationToken = createConfirmationTokenResult.getPayload(Token.class);
            rollbackTasks.push(RollbackTask.create(Token.class, confirmationToken));
            //Send it
            log.info("{} Successfully created {}({}) for user '{}'",
                    TAG, confirmationToken.getTokenType(), confirmationToken.getToken(), createdUser.getUsername());
            OperationResult sendResult = senders.getSender(AccountType.EMAIL).send(confirmationToken, input.getEmail());
            if (sendResult.notOk()) {
                log.warn("{} Unable to send created {} to {}. OpResult: {}",
                        TAG, confirmationToken.getTokenType(), input.getEmail(), sendResult);
                log.warn("{} Requesting Rollback", TAG);
                rollbackService.rollback(rollbackTasks);
            }
        }
        //Create Telegram Confirmation Token
        Token telegramConfirmationToken;
        OperationResult createTelegramTokenResult = tokenService.createTelegramConfirmationToken(createdUser);
        if (createTelegramTokenResult.ok()) {
            telegramConfirmationToken = createTelegramTokenResult.getPayload(Token.class);
            rollbackTasks.push(RollbackTask.create(Token.class, telegramConfirmationToken));
        } else {
            telegramConfirmationToken = null;
            log.warn("{} failed to create Telegram token for {}. OpResult: {}",
                    TAG, createdUser.getUsername(), createTelegramTokenResult);
        }

        //Report success back
        OperationResult success = OperationResult.success();
        return Objects.isNull(telegramConfirmationToken)
                ? success : success.addPayload(TELEGRAM_TOKEN_KEY, telegramConfirmationToken.getToken());
    }
}
