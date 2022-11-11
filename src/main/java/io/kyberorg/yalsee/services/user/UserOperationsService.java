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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Stack;

/**
 * Service, that performs user-related operations;
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

    public OperationResult registerUser(final RegisterUserInput input) {
        //create user
        OperationResult userCreateResult = userService.createUser(input.username(), input.password());
        if (userCreateResult.notOk()) {
            return userCreateResult;
        }
        User createdUser = userCreateResult.getPayload(User.class);
        rollbackTasks.push(RollbackTask.create(User.class, createdUser.getId()));

        //create user settings
        OperationResult userSettingsCreateResult = userSettingsService.createNewSettings(createdUser);
        if (userSettingsCreateResult.notOk()) {
            rollbackService.rollback(rollbackTasks);
            return userSettingsCreateResult;
        }
        UserSettings userSettings = userSettingsCreateResult.getPayload(UserSettings.class);
        rollbackTasks.push(RollbackTask.create(UserSettings.class, userSettings.getId()));

        //create local account
        OperationResult createLocalAccountResult = accountService.createLocalAccount(createdUser);
        if (createLocalAccountResult.notOk()) {
            rollbackService.rollback(rollbackTasks);
            return createLocalAccountResult;
        }
        Account localAccount = createLocalAccountResult.getPayload(Account.class);
        rollbackTasks.push(RollbackTask.create(Account.class, localAccount.getId()));

        //create email account
        OperationResult createEmailAccountResult = accountService.createEmailAccount(createdUser, input.email());
        if (createEmailAccountResult.notOk()) {
            rollbackService.rollback(rollbackTasks);
            return createEmailAccountResult;
        }
        Account emailAccount = createEmailAccountResult.getPayload(Account.class);
        rollbackTasks.push(RollbackTask.create(Account.class, emailAccount.getId()));

        //Settings update main channel
        userSettings.setMainChannel(AccountType.EMAIL);

        //if tfa enabled
        if (input.tfaEnabled()) {
            userSettings.setTfaEnabled(true);
            userSettings.setTfaChannel(AccountType.EMAIL);
        }
        OperationResult saveChannelUpdates = userSettingsService.updateUserSettings(userSettings);
        if (saveChannelUpdates.notOk()) {
            log.error("{} failed to update {}. OpResult: {}",
                    TAG, UserSettings.class.getSimpleName(), saveChannelUpdates);
        }

        //token create confirmation token
        OperationResult createConfirmationTokenResult = tokenService.createConfirmationToken(createdUser, emailAccount);
        if (createConfirmationTokenResult.notOk()) {
            log.error("{} failed to create confirmation token for {}. OpResult: {}",
                    TAG, createdUser.getUsername(), createConfirmationTokenResult);
            return createConfirmationTokenResult;
        }
        Token confirmationToken = createConfirmationTokenResult.getPayload(Token.class);
        rollbackTasks.push(RollbackTask.create(Token.class, confirmationToken.getId()));

        //TODO replace with Senders once ready
        log.info("{} Successfully got confirmation token for {} {}. Now waiting to send it.",
                TAG, confirmationToken, createdUser.getUsername());
        rollbackService.rollback(rollbackTasks);

        //send it
        return OperationResult.success();
    }
}
