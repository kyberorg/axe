package io.kyberorg.yalsee.services.user.rollback;

import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.dao.AccountDao;
import io.kyberorg.yalsee.dao.TokenDao;
import io.kyberorg.yalsee.dao.UserDao;
import io.kyberorg.yalsee.dao.UserSettingsDao;
import io.kyberorg.yalsee.exception.error.YalseeErrorBuilder;
import io.kyberorg.yalsee.models.BaseModel;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Locale;
import java.util.Stack;

/**
 * Service, that performs rollbacks.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RollbackService {
    private final ErrorUtils errorUtils;
    private final AccountDao accountDao;
    private final TokenDao tokenDao;
    private final UserDao userDao;
    private final UserSettingsDao userSettingsDao;

    public static final String TAG = "[" + RollbackService.class.getSimpleName() + "]";
    public static final String ERR_NO_SUCH_DAO = "No corresponding DAO found for given model";

    @Async
    public void rollback(final Stack<RollbackTask> rollbackTasks) {
        log.info("{} Rollback requested with {} tasks", TAG, rollbackTasks.size());
        while (!rollbackTasks.isEmpty()) {
            RollbackTask task = rollbackTasks.pop();
            OperationResult result = performRollback(task);
            if (result.notOk()) {
                StringBuilder message = new StringBuilder("Exception while executing ");
                message.append(task).append(" ");
                message.append("Error: ").append(result.getMessage()).append(" ");
                message.append("Remaining (un-done) tasks are: ");
                if (rollbackTasks.isEmpty()) {
                    message.append("none");
                } else {
                    for (RollbackTask t : rollbackTasks) {
                        message.append(t);
                    }
                }
                reportToBugsnag(message.toString(), HttpCode.SERVER_ERROR, null);
                return;
            }
        }
    }

    private OperationResult performRollback(final RollbackTask task) {
        log.info("{} Starting {}", TAG, task);
        CrudRepository<? extends BaseModel, Long> dao = getDaoByModel(task.getModel());
        if (dao == null) return OperationResult.elementNotFound().withMessage(ERR_NO_SUCH_DAO);
        try {
            dao.deleteById(task.getRecord().getId());
            log.debug("{} {} executed successfully", TAG, task);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            reportToBugsnag("Database is DOWN", HttpCode.APP_IS_DOWN, e);
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on rolling changes back.", TAG);
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    private CrudRepository<? extends BaseModel, Long> getDaoByModel(final Class<? extends BaseModel> model) {
        return switch (model.getSimpleName().toLowerCase(Locale.ROOT)) {
            case "account" -> accountDao;
            case "token" -> tokenDao;
            case "user" -> userDao;
            case "usersettings" -> userSettingsDao;
            default -> null;
        };
    }

    private void reportToBugsnag(final String techMessage, final int code, final Throwable e) {
        errorUtils.reportToBugsnag(YalseeErrorBuilder
                .withTechMessage(techMessage)
                .withMessageToUser("Rollback failed")
                .addRawException(e)
                .withStatus(code)
                .build());
    }
}
