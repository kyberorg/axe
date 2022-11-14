package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.internal.RegisterUserInput;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserSettings;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.rollback.RollbackService;
import io.kyberorg.yalsee.services.user.rollback.RollbackTask;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.users.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Stack;

/**
 * Service, that performs user-related operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserOperationsService {
    private static final String TAG = "[" + UserOperationsService.class.getSimpleName() + "]";
    private final Stack<RollbackTask> rollbackTasks = new Stack<>();
    private final RollbackService rollbackService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final AccountService accountService;
    private final TokenService tokenService;

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

        //Create Local Account
        OperationResult createLocalAccountResult = accountService.createLocalAccount(createdUser);
        if (createLocalAccountResult.notOk()) {
            log.error("{} Failed to create local {}. OpResult: {}",
                    TAG, Account.class.getSimpleName(), createLocalAccountResult);
            rollbackService.rollback(rollbackTasks);
            return createLocalAccountResult;
        }
        Account localAccount = createLocalAccountResult.getPayload(Account.class);
        rollbackTasks.push(RollbackTask.create(Account.class, localAccount));

        //Create Email Account
        OperationResult createEmailAccountResult = accountService.createEmailAccount(createdUser, input.getEmail());
        if (createEmailAccountResult.notOk()) {
            log.error("{} Failed to create email {}. OpResult: {}",
                    TAG, Account.class.getSimpleName(), createEmailAccountResult);
            rollbackService.rollback(rollbackTasks);
            return createEmailAccountResult;
        }
        Account emailAccount = createEmailAccountResult.getPayload(Account.class);
        rollbackTasks.push(RollbackTask.create(Account.class, emailAccount));

        //UserSettings change Main Channel to Email
        userSettings.setMainChannel(AccountType.EMAIL);

        //if TFA (2-factor Auth) enabled
        if (input.isTfaEnabled()) {
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

        //Create Confirmation Token
        OperationResult createConfirmationTokenResult = tokenService.createConfirmationToken(createdUser, emailAccount);
        if (createConfirmationTokenResult.notOk()) {
            log.error("{} failed to create confirmation token for {}. OpResult: {}",
                    TAG, createdUser.getUsername(), createConfirmationTokenResult);
            return createConfirmationTokenResult;
        }
        Token confirmationToken = createConfirmationTokenResult.getPayload(Token.class);
        rollbackTasks.push(RollbackTask.create(Token.class, confirmationToken));

        //Send it
        //TODO replace with Senders once ready
        log.info("{} Successfully created {}({}) for user '{}'. Now waiting to send it.",
                TAG, confirmationToken.getTokenType(), confirmationToken.getToken(), createdUser.getUsername());
        log.warn("{} Unable to send created {} to {} - Senders System is not ready yet.",
                TAG, confirmationToken.getTokenType(), input.getEmail());
        log.warn("{} Requesting Rollback", TAG);
        rollbackService.rollback(rollbackTasks);

        //Report success back
        return OperationResult.success();
    }
}
