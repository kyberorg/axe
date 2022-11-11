package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.internal.RegisterUserInput;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.UserSettings;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.rollback.RollbackService;
import io.kyberorg.yalsee.services.user.rollback.RollbackTask;
import io.kyberorg.yalsee.services.user.rollback.RollbackTasks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service, that performs user-related operations;
 */
@RequiredArgsConstructor
@Service
public class UserOperationsService {
    private final RollbackTasks rollbackTasks = new RollbackTasks();
    private final RollbackService rollbackService;

    private final UserService userService;
    private final UserSettingsService userSettingsService;

    public OperationResult registerUser(final RegisterUserInput input) {
        //TODO implement
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
        //create email account
        //Settings update main channel
        //if tfa enabled
        //Settings set tfa channel
        //token create confirmation token
        //send it
        return OperationResult.success();
    }
}
